package com.example.examplemod.items;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    //获取物品的注册表
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);//第一个参数是注册的物品的类型，第二个是MODID

    //对外暴露的接口，在主类中将注册表添加到事件总线上
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
