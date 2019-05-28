package com.gmail.woodyc40.claimbench;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_13_R2.DedicatedServer;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Benchmark)
public class Bench {
    private final int size = 1000;

    private final UUID[] smallObjects = new UUID[size];
    private final CraftPlayer[] largeObjects = new CraftPlayer[size];

    private final Set<UUID> addUuid = new HashSet<>();
    private final Set<UUID> containsUuid = new HashSet<>();

    private final Set<CraftPlayer> addPlayer = new HashSet<>();
    private final Set<CraftPlayer> containsPlayer = new HashSet<>();

    @Setup
    public void setup() {
        for (int i = 0; i < size; i++) {
            UUID uuid = UUID.randomUUID();
            smallObjects[i] = uuid;

            CraftWorld spawnWorld = ((CraftWorld) Bukkit.getWorld("world"));
            CraftServer server = (CraftServer) Bukkit.getServer();
            DedicatedServer ds = server.getHandle().getServer();
            GameProfile gp = new GameProfile(uuid, "");
            EntityPlayer ep = new EntityPlayer(ds, spawnWorld.getHandle(), gp, new PlayerInteractManager(spawnWorld.getHandle()));

            CraftPlayer player = new CraftPlayer(server, ep);
            largeObjects[i] = player;

            this.containsUuid.add(uuid);
            this.containsPlayer.add(player);
            this.containsPlayer.add(player);
            this.addPlayer.add(player);
        }

        // Clear entries, retain elementData array size
        this.addUuid.clear();
        this.addPlayer.clear();
    }

    @Benchmark
    public UUID a_baselineSmall() {
        int idx = ThreadLocalRandom.current().nextInt(this.smallObjects.length);
        return this.smallObjects[idx];
    }

    @Benchmark
    public boolean a_containsSmall() {
        int idx = ThreadLocalRandom.current().nextInt(this.smallObjects.length);
        UUID uuid = this.smallObjects[idx];
        return this.containsUuid.contains(uuid);
    }

    @Benchmark
    public boolean a_addSmall() {
        int idx = ThreadLocalRandom.current().nextInt(this.smallObjects.length);
        UUID uuid = this.smallObjects[idx];
        return this.addUuid.add(uuid);
    }

    @Benchmark
    public CraftPlayer b_baselineLarge() {
        int idx = ThreadLocalRandom.current().nextInt(this.largeObjects.length);
        return this.largeObjects[idx];
    }

    @Benchmark
    public boolean b_containsLarge() {
        int idx = ThreadLocalRandom.current().nextInt(this.largeObjects.length);
        CraftPlayer player = this.largeObjects[idx];
        return this.containsPlayer.contains(player);
    }

    @Benchmark
    public boolean b_addLarge() {
        int idx = ThreadLocalRandom.current().nextInt(this.largeObjects.length);
        CraftPlayer player = this.largeObjects[idx];
        return this.addPlayer.add(player);
    }
}
