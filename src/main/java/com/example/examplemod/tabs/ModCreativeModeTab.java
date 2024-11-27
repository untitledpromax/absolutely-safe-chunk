package com.example.examplemod.tabs;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.blocks.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    public static final String TEST1_TAB_STRING="creativetab.test1_tab";

    //获取注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS=
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExampleMod.MODID);

    public static final RegistryObject<CreativeModeTab> TEST1_TAB=CREATIVE_MODE_TABS.register("test1_tab",
            ()-> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.TEST_BLOCK.get()))//设置mod物品栏的图标
                    .title(Component.translatable(TEST1_TAB_STRING))//设置mod物品栏的标题
                    //开始向物品栏里添加我们新增的物品
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.TEST_BLOCK.get());
                        pOutput.accept(ModBlocks.TEST_EMPTY.get());
                    })
                    .build());//构建完成

    public static final void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
