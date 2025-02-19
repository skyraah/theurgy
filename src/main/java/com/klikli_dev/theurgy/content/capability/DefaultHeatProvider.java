// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.theurgy.content.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class DefaultHeatProvider implements HeatProvider, INBTSerializable<Tag> {
    protected boolean isHot;

    @Override
    public boolean isHot() {
        return this.isHot;
    }

    public void setHot(boolean isHot) {
        this.isHot = isHot;
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider pRegistries) {
        return ByteTag.valueOf(this.isHot ? (byte) 1 : 0);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider pRegistries, Tag nbt) {
        if (!(nbt instanceof ByteTag byteNbt))
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        this.isHot = byteNbt.getAsByte() != 0;
    }
}
