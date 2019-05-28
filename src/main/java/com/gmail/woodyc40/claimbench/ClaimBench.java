package com.gmail.woodyc40.claimbench;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_13_R2.DedicatedServer;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftWolf;
import org.bukkit.plugin.java.JavaPlugin;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.runner.RunnerException;
import sun.jvm.hotspot.oops.ObjectHeap;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ClaimBench extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            org.openjdk.jmh.Main.main(new String[0]);
        } catch (RunnerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final int size = 1000;

    private final Object[] smallObjects = new Object[size];
    private final Object[] largeObjects = new Object[size];

    private final Set<Object> add = new HashSet<Object>();
    private final Set<Object> contains = new HashSet<Object>();

    @Setup
    public void setup() {
        for (int i = 0; i < size; i++) {
            UUID uuid = UUID.randomUUID();
            smallObjects[i] = uuid;

            CraftWorld spawnWorld = ((CraftWorld) Bukkit.getWorld("world"));
            CraftServer server = (CraftServer) Bukkit.getServer();
            DedicatedServer ds = server.getHandle().getServer();
            GameProfile gp = new GameProfile(uuid, "");
            EntityPlayer ep = new EntityPlayer(ds, spawnWorld.getHandle(), gp, new PlayerInteractManager(spawnWorld.getHandle()))

            largeObjects[i] = new CraftPlayer(server, ep);
        }
    }


}
