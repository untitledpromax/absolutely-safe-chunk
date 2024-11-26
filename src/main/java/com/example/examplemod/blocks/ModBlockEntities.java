package com.example.examplemod.blocks;

import com.example.examplemod.ExampleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ExampleMod.MODID);

    public static final RegistryObject<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("test_block_entity", () ->
                    BlockEntityType.Builder.of(TestBlockEntity::new,
                            ModBlocks.TEST_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
