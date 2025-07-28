package com.businessmc.businessmcmod.client.ui.menu;

import com.businessmc.businessmcmod.BusinessMCMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuRegistration {
    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, BusinessMCMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<BelmondMenu>> BELMOND_MENU =
            MENUS.register("belmond_menu", () -> IMenuTypeExtension.create(BelmondMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BusinessShopMenu>> BUSINESS_SHOP_MENU =
            MENUS.register("business_shop_menu", () -> IMenuTypeExtension.create(BusinessShopMenu::new));

    public MenuRegistration(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}