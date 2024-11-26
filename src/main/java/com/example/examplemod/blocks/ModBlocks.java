package com.example.examplemod.blocks;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.items.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    //获取方块注册表
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MODID);

    public static final RegistryObject<Block> TEST_BLOCK = registryBlock("test_block", TestBlock::new);

    public static final RegistryObject<Block> TEST_ORE = registryBlock("test_ore",
            //返回一个掉落经验的方块
            ()->new DropExperienceBlock(BlockBehaviour.Properties.of()//创建一个空属性
                    .strength(1.5F, 3.0F)//设置硬度和爆炸防护强度
                    , UniformInt.of(1, 2)));//设置经验值掉落范围

    //自定义注册方法方法，因为要同时注册方块和方块对应的物品，就写在一起了
    private static <T extends Block> RegistryObject<T> registryBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);//注册方块
        registerBlockItem(name, toReturn);//注册对应物品
        return toReturn;
    }

    //把方块对应的物品注册的Item注册表上
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    //添加注册
    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
