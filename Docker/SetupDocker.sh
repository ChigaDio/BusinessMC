#!/bin/bash

set -e

# 既存のコンテナをチェックし、minecraft-neoforge が稼働中の場合に Stop.sh を実行
echo "?? 既存の Docker コンテナをチェック..."
if docker ps -q -f name=minecraft-neoforge | grep -q .; then
    echo "? minecraft-neoforge コンテナが稼働中です。Stop.sh を実行..."
    # Stop.sh のパスを /opt/minecraft/Stop.sh に変更
    if docker exec minecraft-neoforge test -f /opt/minecraft/Stop.sh; then
        docker exec minecraft-neoforge /opt/minecraft/Stop.sh
        echo "? 停止待機（10秒）..."
        sleep 10
    else
        echo "? Stop.sh が見つかりません。コンテナの直接停止を試みます..."
        docker stop minecraft-neoforge || true
        echo "? 停止待機（10秒）..."
        sleep 10
    fi
fi

# session.lock を削除
echo "?? session.lock をチェックして削除..."
docker run --rm \
   -v minecraft_data:/opt/minecraft/world busybox \
   sh -c '\
     # session.lock のパスを /opt/minecraft/world/session.lock に変更
     # Minecraftサーバーは通常、ワールドディレクトリの直下に session.lock を作成します
     LOCK="/opt/minecraft/world/session.lock"; \
     if [ -f "$LOCK" ]; then \
       echo "– 削除: $LOCK"; \
       rm "$LOCK"; \
     else \
       echo "– ロックファイル見つからず: $LOCK"; \
     fi' || true

echo "?? docker compose down..."
# -v オプションを削除し、MongoDBとMinecraftのデータボリュームを保持
docker compose down || true

# MongoDBのデータディレクトリを強制的に削除する行は削除しました
# rm -rf ./mongo_data || true # データを保持するためこの行は削除

# docker-compose.yml 生成
cat <<'EOF' > docker-compose.yml
services:
  mongo:
    image: mongo:4.4
    container_name: mongo
    ports:
      - "27117:27017"
    command: ["mongod", "--replSet", "rs0", "--bind_ip_all"]
    volumes:
      - ./mongo_data:/data/db
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "mongo", "--eval", "db.adminCommand('ping')"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s
    # MongoDBコンテナのホスト名を 'mongo' に設定
    hostname: mongo

  minecraft-neoforge:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: minecraft-neoforge
    ports:
      - "25560:25565"
    # ホストの minecraft_data をコンテナの /opt/minecraft/world にマウント
    # これにより、サーバーの実行ファイルはイメージ内に残り、ワールドデータのみが永続化される
    volumes:
      - ./minecraft_data:/opt/minecraft/world
    environment:
      - NEOFORGE_VERSION=21.8.17
    restart: unless-stopped
    tty: true
    stdin_open: true
    depends_on:
      mongo:
        condition: service_healthy

volumes:
  mongo_data:
  minecraft_data:
EOF

# Dockerfile 生成（NeoForge用）
cat <<'EOF' > Dockerfile
FROM openjdk:21-jdk-slim
# 作業ディレクトリを /opt/minecraft に設定
WORKDIR /opt/minecraft

# NeoForge バージョンを環境変数として設定
ENV NEOFORGE_VERSION=21.8.17

# curl をインストール
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# NeoForge インストーラーをダウンロード
RUN echo "Downloading NeoForge installer version ${NEOFORGE_VERSION}..." && \
    curl -f -O https://maven.neoforged.net/releases/net/neoforged/neoforge/${NEOFORGE_VERSION}/neoforge-${NEOFORGE_VERSION}-installer.jar || \
    { echo "Failed to download NeoForge installer"; exit 1; }

# NeoForge サーバーをインストール
# インストーラーは現在の WORKDIR (/opt/minecraft) にサーバーファイルを配置する
RUN echo "Installing NeoForge server..." && \
    java -jar neoforge-${NEOFORGE_VERSION}-installer.jar --installServer > installer.log 2>&1 || \
    { echo "Failed to install NeoForge server"; cat installer.log; exit 1; }

# インストーラーファイルをクリーンアップ
RUN rm neoforge-${NEOFORGE_VERSION}-installer.jar installer.log

# EULA ファイルを作成
RUN echo "eula=true" > eula.txt

# JVM 引数ファイルを作成
RUN cat <<'INNER_EOF' > user_jvm_args.txt
-Xms3G
-Xmx3G
INNER_EOF

# run.sh スクリプトを作成
RUN cat <<'INNER_EOF' > run.sh
#!/bin/bash
# NeoForge サーバーJARを実行する
# unix_args.txt は NeoForge インストーラーによって生成され、サーバーJARを指す
# 'nogui' はサーバーがGUIなしで実行されることを保証する
java @user_jvm_args.txt @libraries/net/neoforged/neoforge/${NEOFORGE_VERSION}/unix_args.txt nogui
INNER_EOF

# run.sh を実行可能にする
RUN chmod +x run.sh

# Stop.sh スクリプトを作成
# このスクリプトはサーバーの標準入力 (プロセスID 1の標準入力) に 'stop' コマンドを送信する
RUN cat <<'INNER_EOF' > Stop.sh
#!/bin/bash
echo stop > /proc/1/fd/0
INNER_EOF

# Stop.sh を実行可能にする
RUN chmod +x Stop.sh

# server.properties から server-ip の設定を削除
# これにより、Minecraftサーバーはすべての利用可能なインターフェースにバインドされます
RUN if [ -f server.properties ]; then \
    sed -i '/server-ip=/d' server.properties; \
    echo "server.properties から server-ip を削除しました。"; \
else \
    echo "server.properties が見つかりません。server-ip の変更はスキップされました。"; \
fi

# デフォルトのMinecraftサーバーポートを公開
EXPOSE 25565

# コンテナ起動時に実行するデフォルトコマンドを設定
# これにより WORKDIR (/opt/minecraft) から run.sh スクリプトが実行される
CMD ["./run.sh"]
EOF

# Dockerfile の内容を確認
echo "?? Generated Dockerfile content:"
cat Dockerfile

# restart.sh 生成
cat <<'EOF' > restart.sh
#!/bin/bash

set -e

echo "? minecraft-neoforge を停止..."
# Stop.sh のパスを /opt/minecraft/Stop.sh に変更
if docker exec minecraft-neoforge test -f /opt/minecraft/Stop.sh; then
    docker exec minecraft-neoforge /opt/minecraft/Stop.sh
else
    echo "? Stop.sh が見つかりません。コンテナの直接停止を試みます..."
    docker stop minecraft-neoforge || true
fi
echo "? 停止待機（10秒）..."
sleep 10
echo "? minecraft-neoforge を再起動..."
docker-compose restart minecraft-neoforge
echo "? 再起動完了。ログを確認してください。"
docker-compose logs minecraft-neoforge
EOF
chmod +x restart.sh

# login.sh 生成
cat <<'EOF' > login.sh
#!/bin/bash

set -e

echo "? Minecraftサーバーコンソールに接続中..."
echo "? 接続後、サーバーにコマンドを入力できます。"
echo "? サーバーを停止せずにコンソールから抜けるには、Ctrl+P を押してから Ctrl+Q を押してください。"
echo "? 注意: Ctrl+C を押すと、Minecraftサーバープロセスが停止する可能性があります。"
docker attach minecraft-neoforge
EOF
chmod +x login.sh

echo "?? docker compose build..."
docker compose build
echo "?? docker compose up..."
docker compose up -d

# MongoDB コンテナの起動待機 (healthcheckが通るまで)
CONTAINER_NAME="mongo"
for i in {1..30}; do
    STATUS=$(docker inspect -f '{{.State.Status}}' "$CONTAINER_NAME" 2>/dev/null || echo "notfound")
    if [[ "$STATUS" == "running" ]]; then
        echo "? MongoDB コンテナが起動しました"
        break
    fi
    echo "? MongoDB コンテナ起動待機中... ($i)"
    sleep 2
done

if [[ "$STATUS" != "running" ]]; then
    echo "? MongoDB コンテナが起動しませんでした。ログを確認してください。"
    docker compose logs "$CONTAINER_NAME"
    exit 1
fi

# MongoDBが完全に起動し、コマンドを受け付けるまで待機 (pingで確認)
echo "? MongoDBがコマンドを受け付けるまで待機..."
for i in {1..60}; do
    if docker exec mongo mongo --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
        echo "? MongoDBがコマンドを受け付けました"
        break
    fi
    echo "? MongoDB応答待機中... ($i)"
    sleep 1
done

if ! docker exec mongo mongo --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
    echo "? MongoDBが完全に起動しませんでした。ログを確認してください。"
    docker compose logs mongo
    exit 1
fi

# レプリカセットが既に初期化されているか確認し、必要であれば初期化
echo "? レプリカセットの状態を確認..."
# rs.status() を使ってレプリカセットの状態をより詳細に確認
# 'ok' が 1 で 'set' が存在すれば、初期化済みと判断
IS_REPLICA_SET_INITIALIZED=$(docker exec mongo mongo --quiet --eval "try { var status = rs.status(); if (status.ok == 1 && status.set) { print('true'); } else { print('false'); } } catch (e) { print('false'); }" 2>/dev/null)

if [[ "$IS_REPLICA_SET_INITIALIZED" == "true" ]]; then
    echo "? レプリカセットは既に初期化され、正常に稼働しています。"
else
    echo "? レプリカセットは初期化されていません。初期化を試みます..."
    # 初期化コマンドの実行前に少し待機
    sleep 2 # MongoDBが完全に安定するのを待つための追加の待機
    if ! docker exec mongo mongo --eval 'var status = rs.initiate({ _id: "rs0", members: [{ _id: 0, host: "mongo:27017" }] }); printjson(status);' | grep -q '"ok" : 1'; then
        echo "? rs.initiate() に失敗しました。ログを確認してください。"
        docker compose logs mongo
        exit 1
    fi
    echo "? レプリカセットの初期化を試みました。"

    # PRIMARY 確定まで待つ
    for i in {1..30}; do
      ismaster=$(docker exec mongo mongo --quiet --eval "db.isMaster().ismaster" 2>/dev/null || echo "false")
      if [[ "$ismaster" == "true" ]]; then
        echo "? レプリカセットが Primary に昇格しました"
        break
      fi
      echo "? Primary 昇格待機中... ($i)"
      sleep 2
    done

    # Primary に昇格しなかった場合はエラー終了
    if [[ "$ismaster" != "true" ]]; then
        echo "? MongoDB レプリカセットが Primary に昇格しませんでした。ログを確認してください。"
        docker compose logs mongo
        exit 1
    fi

    # Primary 昇格後、少し待機して安定を確保
    echo "? Primary 昇格後、安定待機（5秒）..."
    sleep 5
fi

# admin ユーザー作成
docker exec mongo mongo --eval '
  var admin = db.getSiblingDB("admin");
  if (!admin.getUser("admin")) {
    admin.createUser({
      user: "admin",
      pwd: "secret",
      roles: [{role: "root", db: "admin"}]
    });
    print("? admin ユーザーを作成しました");
  } else {
    print("?? admin ユーザーは既に存在します");
  }
'

# NeoForge コンテナの起動待機
CONTAINER_NAME="minecraft-neoforge"
for i in {1..30}; do
    STATUS=$(docker inspect -f '{{.State.Status}}' "$CONTAINER_NAME" 2>/dev/null || echo "notfound")
    if [[ "$STATUS" == "running" ]]; then
        echo "? NeoForge コンテナが起動しました"
        break
    fi
    echo "? NeoForge コンテナ起動待機中... ($i)"
    sleep 2
done

if [[ "$STATUS" != "running" ]]; then
    echo "? NeoForge コンテナが起動しませんでした。ログを確認してください。"
    docker compose logs "$CONTAINER_NAME"
    exit 1
fi

# NeoForge の起動待機（60秒）
echo "? NeoForge の起動待機（60秒）..."
sleep 60

# サーバーログで起動完了を確認
# ログファイルのパスが /opt/minecraft/logs/latest.log になることを想定
if docker exec minecraft-neoforge grep -q "Done" /opt/minecraft/logs/latest.log; then
    echo "? NeoForge サーバーが正常に起動しました"
else
    echo "? NeoForge サーバーの起動に失敗しました。ログを確認してください。"
    docker compose logs minecraft-neoforge
    exit 1
fi

echo "? 完了しました。MongoDB と NeoForge サーバーが起動し、設定が完了しました。"
