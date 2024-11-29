package com.example.examplemod.blocks;

import com.example.examplemod.render.RenderSurface;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class TestBlockEntity extends BlockEntity {

    private static final String IN_CHUNK_POS_FIELD = "inChunkPos";
    @Getter
    private static final Map<ChunkPos, Integer> chunkPosCntMap = new HashMap<>();
    @Getter
    private static final Set<RenderSurface> renderSurfaces = new HashSet<>();
    private static final Gson gson = new Gson();

    private ChunkPos inChunkPos;

    public TestBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TEST_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void addHasBlockChunkPos(ChunkPos pos) {
        chunkPosCntMap.put(pos, chunkPosCntMap.getOrDefault(pos, 0) + 1);
        if (hasEastNeighbor(pos)) {
            renderSurfaces.remove(getEastSide(pos));
        } else {
            renderSurfaces.add(getEastSide(pos));
        }
        if (hasNorthNeighbor(pos)) {
            renderSurfaces.remove(getNorthSide(pos));
        } else {
            renderSurfaces.add(getNorthSide(pos));
        }
        if (hasWestNeighbor(pos)) {
            renderSurfaces.remove(getWestSide(pos));
        } else {
            renderSurfaces.add(getWestSide(pos));
        }
        if (hasSouthNeighbor(pos)) {
            renderSurfaces.remove(getSouthSide(pos));
        } else {
            renderSurfaces.add(getSouthSide(pos));
        }
        this.inChunkPos = pos;
        setChanged();
    }

    public void removeHasBlockChunkPos(ChunkPos pos) {
        if (!chunkPosCntMap.containsKey(pos)) {
            return;
        }
        chunkPosCntMap.replace(pos, chunkPosCntMap.get(pos) - 1);
        if (chunkPosCntMap.get(pos).compareTo(0) <= 0) {
            chunkPosCntMap.remove(pos);
            if (hasEastNeighbor(pos)) {
                renderSurfaces.add(getEastSide(pos));
            } else {
                renderSurfaces.remove(getEastSide(pos));
            }
            if (hasNorthNeighbor(pos)) {
                renderSurfaces.add(getNorthSide(pos));
            } else {
                renderSurfaces.remove(getNorthSide(pos));
            }
            if (hasWestNeighbor(pos)) {
                renderSurfaces.add(getWestSide(pos));
            } else {
                renderSurfaces.remove(getWestSide(pos));
            }
            if (hasSouthNeighbor(pos)) {
                renderSurfaces.add(getSouthSide(pos));
            } else {
                renderSurfaces.remove(getSouthSide(pos));
            }
        }
        this.inChunkPos = null;
        setChanged();
    }

    public static void removeAll() {
        chunkPosCntMap.clear();
        renderSurfaces.clear();
    }

    public static boolean didEnterSafeChunk(ChunkPos pos) {
        return chunkPosCntMap.containsKey(pos);
    }

    public static void showChunkPosMap() {
        log.info("content in map:{}", chunkPosCntMap);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadClientData(tag);
    }

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

    private RenderSurface getWestSide(ChunkPos chunkPos) {
        return new RenderSurface.EastAndWestSide(chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockZ() + 1);
    }

    private RenderSurface getEastSide(ChunkPos chunkPos) {
        return new RenderSurface.EastAndWestSide(chunkPos.getMaxBlockX() + 1, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockZ() + 1);
    }

    private RenderSurface getNorthSide(ChunkPos chunkPos) {
        return new RenderSurface.NorthAndSouthSide(chunkPos.getMinBlockZ(), chunkPos.getMinBlockX(), chunkPos.getMaxBlockX() + 1);
    }

    private RenderSurface getSouthSide(ChunkPos chunkPos) {
        return new RenderSurface.NorthAndSouthSide(chunkPos.getMaxBlockZ() + 1, chunkPos.getMinBlockX(), chunkPos.getMaxBlockX() + 1);
    }

    private boolean hasWestNeighbor(ChunkPos pos) {
        return chunkPosCntMap.containsKey(new ChunkPos(pos.x - 1, pos.z));
    }

    private boolean hasEastNeighbor(ChunkPos pos) {
        return chunkPosCntMap.containsKey(new ChunkPos(pos.x + 1, pos.z));
    }

    private boolean hasNorthNeighbor(ChunkPos pos) {
        return chunkPosCntMap.containsKey(new ChunkPos(pos.x, pos.z - 1));
    }

    private boolean hasSouthNeighbor(ChunkPos pos) {
        return chunkPosCntMap.containsKey(new ChunkPos(pos.x, pos.z + 1));
    }
}
