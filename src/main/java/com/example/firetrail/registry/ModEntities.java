package com.example.firetrail.registry;

import com.example.firetrail.FireTrailMod;
import com.example.firetrail.entity.FireTrailEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, FireTrailMod.MODID);

    public static final RegistryObject<EntityType<FireTrailEntity>> FIRE_TRAIL =
            ENTITIES.register("fire_trail", () ->
                    EntityType.Builder.<FireTrailEntity>of(FireTrailEntity::new, MobCategory.MISC)
                            .sized(0.35F, 0.35F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(FireTrailMod.MODID + ":fire_trail"));

    private ModEntities() {}
}
