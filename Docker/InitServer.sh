#!/bin/bash

set -e

echo "?? システムの更新..."
dnf -y update

echo "?? Docker のインストール..."
dnf -y install dnf-plugins-core
dnf config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
dnf -y install docker-ce docker-ce-cli containerd.io docker-compose-plugin

echo "?? Docker を有効化・起動..."
systemctl enable --now docker

echo "?? Docker Compose が使えるか確認..."
docker compose version

echo "?? スワップファイル（10GB）作成..."
SWAPFILE="/swapfile"
fallocate -l 10G $SWAPFILE
chmod 600 $SWAPFILE
mkswap $SWAPFILE
swapon $SWAPFILE

echo "$SWAPFILE swap swap defaults 0 0" >> /etc/fstab

echo "?? firewalld によるポート開放..."
# firewalld が有効でなければ有効化
systemctl enable --now firewalld

# SSHは通常開いているが念のため
firewall-cmd --permanent --add-service=ssh

# TCP+UDP 25550 ポート開放
firewall-cmd --permanent --add-port=25560/tcp
firewall-cmd --permanent --add-port=25560/udp

# TCP 27117 ポート開放
firewall-cmd --permanent --add-port=27117/tcp

# 設定をリロード
firewall-cmd --reload

echo "? 完了しました！"

