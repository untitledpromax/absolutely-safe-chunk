package com.example.examplemod.blocks;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

@Slf4j
public class TestBlock extends Block implements EntityBlock {

    public TestBlock() {
        super(BlockBehaviour.Properties.of()//创建一个空属性
                .strength(1.5F, 6.0F));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(!pLevel.isClientSide() && pHand == InteractionHand.MAIN_HAND){
            pPlayer.sendSystemMessage(Component.literal("current chunk pos:" + new ChunkPos(pPos)));
            // TODO: delete the next line after debugging
            TestBlockEntity.showChunkPosMap();
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void onRemove(BlockState state_1, Level level, BlockPos pos, BlockState state_2, boolean flag) {
        if (!level.isClientSide()) {
            ChunkPos chunkPos = level.getChunkAt(pos).getPos();
            TestBlockEntity blockEntity = (TestBlockEntity) level.getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntity.removeHasBlockChunkPos(chunkPos);
            }
        }
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if(!pLevel.isClientSide()) {
            ChunkPos chunkPos = pLevel.getChunkAt(pPos).getPos();
            TestBlockEntity blockEntity = (TestBlockEntity) pLevel.getBlockEntity(pPos);
            if (blockEntity != null) {
                blockEntity.addHasBlockChunkPos(chunkPos);
            }
            List<Monster> monsterList = pLevel.getEntitiesOfClass(Monster.class, new AABB(chunkPos.getMinBlockX(), -64, chunkPos.getMinBlockZ(),
                    chunkPos.getMaxBlockX(), 320, chunkPos.getMaxBlockZ()));
            monsterList.forEach(monster -> {
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, pLevel);
                lightningBolt.setPos(monster.position());
                lightningBolt.setVisualOnly(true);
                pLevel.addFreshEntity(lightningBolt);
                monster.kill();
            });
        }

        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TestBlockEntity(blockPos, blockState);
    }
}
