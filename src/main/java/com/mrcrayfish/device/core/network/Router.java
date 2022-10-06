package com.mrcrayfish.device.core.network;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.tileentity.TileEntityNetworkDevice;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class Router
{
    private final Map<UUID, NetworkDevice> NETWORK_DEVICES = new HashMap<>();

    private int timer;
    private UUID routerId;
    private BlockPos pos;

    public Router(BlockPos pos)
    {
        this.pos = pos;
    }

    public void update(Level Level)
    {
        if(++timer >= DeviceConfig.BEACON_INTERVAL.get())
        {
            sendBeacon(Level);
            timer = 0;
        }
    }

    public boolean addDevice(UUID id, String name)
    {
        if(NETWORK_DEVICES.size() >= DeviceConfig.MAX_DEVICES.get())
        {
            return NETWORK_DEVICES.containsKey(id);
        }
        if(!NETWORK_DEVICES.containsKey(id))
        {
            NETWORK_DEVICES.put(id, new NetworkDevice(id, name, this));
        }
        timer = DeviceConfig.BEACON_INTERVAL.get();
        return true;
    }

    public boolean addDevice(TileEntityNetworkDevice device)
    {
        if(NETWORK_DEVICES.size() >= DeviceConfig.MAX_DEVICES.get())
        {
            return NETWORK_DEVICES.containsKey(device.getId());
        }
        if(!NETWORK_DEVICES.containsKey(device.getId()))
        {
            NETWORK_DEVICES.put(device.getId(), new NetworkDevice(device));
        }
        return true;
    }

    public boolean isDeviceRegistered(TileEntityNetworkDevice device)
    {
        return NETWORK_DEVICES.containsKey(device.getId());
    }

    public boolean isDeviceConnected(TileEntityNetworkDevice device)
    {
        return isDeviceRegistered(device) && NETWORK_DEVICES.get(device.getId()).getPos() != null;
    }

    public void removeDevice(TileEntityNetworkDevice device)
    {
        NETWORK_DEVICES.remove(device.getId());
    }

    @Nullable
    public TileEntityNetworkDevice getDevice(Level Level, UUID id)
    {
        return NETWORK_DEVICES.containsKey(id) ? NETWORK_DEVICES.get(id).getDevice(Level) : null;
    }

    public Collection<NetworkDevice> getNetworkDevices()
    {
        return NETWORK_DEVICES.values();
    }

    public Collection<NetworkDevice> getConnectedDevices(Level level)
    {
        sendBeacon(level);
        return NETWORK_DEVICES.values().stream().filter(networkDevice -> networkDevice.getPos() != null).collect(Collectors.toList());
    }

    public Collection<NetworkDevice> getConnectedDevices(final Level Level, Class<? extends TileEntityNetworkDevice> type)
    {
        final Predicate<NetworkDevice> DEVICE_TYPE = networkDevice ->
        {
            if(networkDevice.getPos() == null)
                return false;

            BlockEntity tileEntity = Level.getBlockEntity(networkDevice.getPos());
            if(tileEntity instanceof TileEntityNetworkDevice)
            {
                return type.isAssignableFrom(tileEntity.getClass());
            }
            return false;
        };
        return getConnectedDevices(Level).stream().filter(DEVICE_TYPE).collect(Collectors.toList());
    }

    private void sendBeacon(Level Level)
    {
        if(Level.isClientSide)
            return;

        NETWORK_DEVICES.forEach((id, device) -> device.setPos(null));
        int range = DeviceConfig.SIGNAL_RANGE.get();
        for(int y = -range; y < range + 1; y++)
        {
            for(int z = -range; z < range + 1; z++)
            {
                for(int x = -range; x < range + 1; x++)
                {
                    BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockEntity tileEntity = Level.getBlockEntity(currentPos);
                    if(tileEntity instanceof TileEntityNetworkDevice tileEntityNetworkDevice)
                    {
                        if(!NETWORK_DEVICES.containsKey(tileEntityNetworkDevice.getId()))
                            continue;
                        if(tileEntityNetworkDevice.receiveBeacon(this))
                        {
                            NETWORK_DEVICES.get(tileEntityNetworkDevice.getId()).update(tileEntityNetworkDevice);
                        }
                    }
                }
            }
        }
    }

    public UUID getId()
    {
        if(routerId == null)
        {
            routerId = UUID.randomUUID();
        }
        return routerId;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public void setPos(BlockPos pos)
    {
        this.pos = pos;
    }

    public CompoundTag toTag(boolean includePos)
    {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("id", getId());

        ListTag deviceList = new ListTag();
        NETWORK_DEVICES.forEach((id, device) -> {
            deviceList.add(device.toTag(includePos));
        });
        tag.put("network_devices", deviceList);

        return tag;
    }

    public static Router fromTag(BlockPos pos, CompoundTag tag)
    {
        Router router = new Router(pos);
        router.routerId = tag.getUUID("id");

        ListTag deviceList = tag.getList("network_devices", Tag.TAG_COMPOUND);
        for(int i = 0; i < deviceList.size(); i++)
        {
            NetworkDevice device = NetworkDevice.fromTag(deviceList.getCompound(i));
            router.NETWORK_DEVICES.put(device.getId(), device);
        }
        return router;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(!(obj instanceof Router router))
            return false;
        return router.getId().equals(routerId);
    }
}
