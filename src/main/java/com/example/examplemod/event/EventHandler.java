package com.example.examplemod.event;

import com.example.examplemod.blocks.TestBlockEntity;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityEvent.EnteringSection;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Slf4j
@Mod.EventBusSubscriber
public class EventHandler {

    @SubscribeEvent
    public static void monsterEnteringChunkEvent(EnteringSection event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Monster)) {
            return;
        }
        Level level = entity.level();
        if (event.didChunkChange() && !level.isClientSide() && TestBlockEntity.didEnterSafeChunk(event.getNewPos().chunk())) {
            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            lightningBolt.setPos(entity.position());
            lightningBolt.setVisualOnly(true);
            level.addFreshEntity(lightningBolt);
            entity.kill();
        }
    }

    @SubscribeEvent
    public static void serverStopEvent(ServerStoppedEvent event) {
        log.info("server stopped");
        TestBlockEntity.removeAll();
    }

}
