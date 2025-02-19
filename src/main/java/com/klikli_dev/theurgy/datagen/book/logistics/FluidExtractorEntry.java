// SPDX-FileCopyrightText: 2024 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.theurgy.datagen.book.logistics;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.theurgy.Theurgy;
import com.klikli_dev.theurgy.datagen.book.LogisticsCategory;
import com.klikli_dev.theurgy.registry.ItemRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.crafting.Ingredient;

public class FluidExtractorEntry extends EntryProvider {
    public static final String ENTRY_ID = "fluid_extractor";

    public FluidExtractorEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("extractor", () -> BookSpotlightPageModel.create()
                .withItem(Ingredient.of(ItemRegistry.LOGISTICS_FLUID_EXTRACTOR.get()))
                .withText(this.context().pageText()));
        this.pageText("""
                When attached to a block, the extractor will extract fluids from the block into the Mercurial Logistics System. The fluids will be inserted into blocks that have inserters attached to them, if the inserters are part of the same network.
                """
        );

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Theurgy.loc("crafting/shaped/logistics_fluid_extractor")));

        this.page("usage", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Usage");
        this.pageText("""
               Right-click a block that has a tank to attach the extractor to it.
               \\
               \\
               The extractor will by default extract from the face it is attached to. This only matters if the block has multiple fluid tanks accessible from different block faces.
               """
        );

        this.page("identification", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Identification");
        this.pageText("""
               The extractor features a red band on the side attached to the target block.
               """
        );

        this.page("direction", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Changing Direction");
        this.pageText("""
               Extractors can also extract from faces they are not attached to. Use the {0} in the "Select Direction" mode to cycle through the extract directions.
                """,
                this.entryLink("Mercurial Wand", LogisticsCategory.CATEGORY_ID, MercurialWandEntry.ENTRY_ID)
        );
    }

    @Override
    protected String entryName() {
        return "Mercurial Fluid Extractor";
    }

    @Override
    protected String entryDescription() {
        return "Extracting fluids from Blocks into the Mercurial Logistics System.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ItemRegistry.LOGISTICS_FLUID_EXTRACTOR.get());
    }

    @Override
    protected String entryId() {
        return ENTRY_ID;
    }
}
