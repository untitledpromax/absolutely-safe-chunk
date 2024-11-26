package com.example.examplemod.blocks;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
public class TestBlockEntity extends BlockEntity {

    private static final String IN_CHUNK_POS_FIELD = "inChunkPos";

    private static Map<ChunkPos, Integer> chunkPosCntMap;

    private ChunkPos inChunkPos;

    private static final Gson gson = new Gson();

    public TestBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TEST_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void addHasBlockChunkPos(ChunkPos pos) {
        if (chunkPosCntMap == null) {
            chunkPosCntMap = new HashMap<>();
        }
        chunkPosCntMap.put(pos, chunkPosCntMap.getOrDefault(pos, 0) + 1);
        this.inChunkPos = pos;
        setChanged();
    }

    public void removeHasBlockChunkPos(ChunkPos pos) {
        if (chunkPosCntMap == null || !chunkPosCntMap.containsKey(pos)) {
            return;
        }
        chunkPosCntMap.replace(pos, chunkPosCntMap.get(pos) - 1);
        if (chunkPosCntMap.get(pos).compareTo(0) <= 0) {
            chunkPosCntMap.remove(pos);
        }
        this.inChunkPos = null;
        setChanged();
    }

    public static void removeAll() {
        if (chunkPosCntMap == null) {
            return;
        }
        chunkPosCntMap.clear();
    }

    public static boolean didEnterSafeChunk(ChunkPos pos) {
        return chunkPosCntMap != null && chunkPosCntMap.containsKey(pos);
    }

    private ChunkPos getInChunkPos(BlockPos blockPos) {
        return new ChunkPos(blockPos.getX() / 16, blockPos.getZ() / 16);
    }

    public static void showChunkPosMap() {
        log.info("content in map:{}", chunkPosCntMap);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadClientData(tag);
    }

//    @Override
//    public CompoundTag getUpdateTag() {
//        CompoundTag tag = super.getUpdateTag();
//        saveClientData(tag);
//        return tag;
//    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveClientData(tag);
    }

    private void saveClientData(CompoundTag tag) {
        tag.putString(IN_CHUNK_POS_FIELD, this.inChunkPos == null ? "" : gson.toJson(inChunkPos));
    }

    private void loadClientData(CompoundTag tag) {
        String tagString = tag.getString(IN_CHUNK_POS_FIELD);
        if (StringUtils.isNotBlank(tagString)) {
            addHasBlockChunkPos(gson.fromJson(tagString, ChunkPos.class));
        }
    }
}
