// SPDX-FileCopyrightText: 2022 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.theurgy.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.theurgy.Theurgy;
import com.klikli_dev.theurgy.content.item.derivative.AlchemicalDerivativeItem;
import com.klikli_dev.theurgy.content.item.niter.AlchemicalNiterItem;
import com.klikli_dev.theurgy.content.item.sulfur.AlchemicalSulfurItem;
import com.klikli_dev.theurgy.content.item.derivative.AlchemicalDerivativeTier;
import com.klikli_dev.theurgy.content.recipe.ReformationRecipe;
import com.klikli_dev.theurgy.datagen.SulfurMappings;
import com.klikli_dev.theurgy.registry.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

public class ReformationRecipeProvider extends JsonRecipeProvider {

    public static final int TIME = ReformationRecipe.DEFAULT_TIME;
    private final Map<AlchemicalDerivativeTier, Integer> fluxPerTier = Map.of(
            AlchemicalDerivativeTier.ABUNDANT, 50,
            AlchemicalDerivativeTier.COMMON, 100,
            AlchemicalDerivativeTier.RARE, 150,
            AlchemicalDerivativeTier.PRECIOUS, 200
    );
    private final Map<ResourceLocation, JsonObject> recipeCache = new HashMap<>();
    private Set<AlchemicalDerivativeItem> noAutomaticRecipesFor = Set.of();

    public ReformationRecipeProvider(PackOutput packOutput) {
        super(packOutput, Theurgy.MODID, "reformation");
    }

    private int getFlux(AlchemicalDerivativeItem item) {
        return this.fluxPerTier.get(item.tier());
    }

    private void makeXtoXRecipes(List<Pair<List<AlchemicalSulfurItem>, TagKey<Item>>> sulfurToTag) {
        sulfurToTag.forEach((entry) -> {
            entry.getFirst().stream().filter(s -> !this.noAutomaticRecipesFor.contains(s))
                    .forEach((sulfur) -> {
                        this.makeTagRecipe(sulfur, entry.getSecond(), this.getFlux(sulfur));
                    });
        });
    }

    /**
     * Convert N of the tag to 1 sulfur
     */
    private void makeNYtoXRecipes(int n, List<Pair<List<AlchemicalSulfurItem>, TagKey<Item>>> sulfurToTag) {
        sulfurToTag.forEach((entry) -> {
            entry.getFirst().stream().filter(s -> !this.noAutomaticRecipesFor.contains(s))
                    .forEach((sulfur) -> {
                        this.makeTagRecipe(sulfur, Collections.nCopies(n, entry.getSecond()), this.getFlux(sulfur));
                    });
        });
    }

    /**
     * Convert 1 of the tag to N sulfur
     */
    private void makeYtoNXRecipes(int n, List<Pair<List<AlchemicalSulfurItem>, TagKey<Item>>> sulfurToTag) {
        sulfurToTag.forEach((entry) -> {
            entry.getFirst().stream().filter(s -> !this.noAutomaticRecipesFor.contains(s))
                    .forEach((sulfur) -> {
                        this.makeTagRecipe(sulfur, n, entry.getSecond(), this.getFlux(sulfur));
                    });
        });
    }

    private void makeNiterToSulfurRecipe(AlchemicalNiterItem source, List<AlchemicalSulfurItem> targets) {
        targets.stream().filter(t -> !this.noAutomaticRecipesFor.contains(t)).forEach((target) -> {
            this.makeRecipe(target, source, this.getFlux(target));
        });
    }

    private void makeNiterToNiterRecipe(AlchemicalNiterItem source, int sourceCount, AlchemicalNiterItem target, int targetCount) {
        this.makeRecipe(target, targetCount, source, sourceCount, this.getFlux(target));
    }

    private void metals() {
        //Add conversion from the niter (representing the whole tier) to the single specific sulfurs
        //This enables conversion between tiers by way of digestion
        this.makeNiterToSulfurRecipe(NiterRegistry.METALS_ABUNDANT.get(), SulfurMappings.metalsAbundant());
        this.makeNiterToSulfurRecipe(NiterRegistry.METALS_COMMON.get(), SulfurMappings.metalsCommon());
        this.makeNiterToSulfurRecipe(NiterRegistry.METALS_RARE.get(), SulfurMappings.metalsRare());
        this.makeNiterToSulfurRecipe(NiterRegistry.METALS_PRECIOUS.get(), SulfurMappings.metalsPrecious());

        //Also allow direct conversion between specific sulfurs of the same tier
        var metalsFromMetals = List.of(
                Pair.of(SulfurMappings.metalsAbundant(), ItemTagRegistry.ALCHEMICAL_SULFURS_METALS_ABUNDANT),
                Pair.of(SulfurMappings.metalsCommon(), ItemTagRegistry.ALCHEMICAL_SULFURS_METALS_COMMON),
                Pair.of(SulfurMappings.metalsRare(), ItemTagRegistry.ALCHEMICAL_SULFURS_METALS_RARE),
                Pair.of(SulfurMappings.metalsPrecious(), ItemTagRegistry.ALCHEMICAL_SULFURS_METALS_PRECIOUS)
        );
        this.makeXtoXRecipes(metalsFromMetals);

        //Further, allow conversion between types
        this.makeNiterToNiterRecipe(NiterRegistry.OTHER_MINERALS_ABUNDANT.get(), 2, NiterRegistry.METALS_ABUNDANT.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.OTHER_MINERALS_COMMON.get(), 2, NiterRegistry.METALS_COMMON.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.OTHER_MINERALS_RARE.get(), 2, NiterRegistry.METALS_RARE.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.OTHER_MINERALS_PRECIOUS.get(), 2, NiterRegistry.METALS_PRECIOUS.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.GEMS_ABUNDANT.get(), 1, NiterRegistry.METALS_ABUNDANT.get(), 2);
        this.makeNiterToNiterRecipe(NiterRegistry.GEMS_COMMON.get(), 1, NiterRegistry.METALS_COMMON.get(), 2);
        this.makeNiterToNiterRecipe(NiterRegistry.GEMS_RARE.get(), 1, NiterRegistry.METALS_RARE.get(), 2);
        this.makeNiterToNiterRecipe(NiterRegistry.GEMS_PRECIOUS.get(), 1, NiterRegistry.METALS_PRECIOUS.get(), 2);
    }

    private void gems() {
        //Add conversion from the niter (representing the whole tier) to the single specific sulfurs
        //This enables conversion between tiers by way of digestion
        this.makeNiterToSulfurRecipe(NiterRegistry.GEMS_ABUNDANT.get(), SulfurMappings.gemsAbundant());
        this.makeNiterToSulfurRecipe(NiterRegistry.GEMS_COMMON.get(), SulfurMappings.gemsCommon());
        this.makeNiterToSulfurRecipe(NiterRegistry.GEMS_RARE.get(), SulfurMappings.gemsRare());
        this.makeNiterToSulfurRecipe(NiterRegistry.GEMS_PRECIOUS.get(), SulfurMappings.gemsPrecious());

        //Also allow direct conversion between specific sulfurs of the same tier
        var gemsFromGems = List.of(
                Pair.of(SulfurMappings.gemsAbundant(), ItemTagRegistry.ALCHEMICAL_SULFURS_GEMS_ABUNDANT),
                Pair.of(SulfurMappings.gemsCommon(), ItemTagRegistry.ALCHEMICAL_SULFURS_GEMS_COMMON),
                Pair.of(SulfurMappings.gemsRare(), ItemTagRegistry.ALCHEMICAL_SULFURS_GEMS_RARE),
                Pair.of(SulfurMappings.gemsPrecious(), ItemTagRegistry.ALCHEMICAL_SULFURS_GEMS_PRECIOUS)
        );
        this.makeXtoXRecipes(gemsFromGems);

        //Further, allow conversion between types
        this.makeNiterToNiterRecipe(NiterRegistry.METALS_ABUNDANT.get(), 2, NiterRegistry.GEMS_ABUNDANT.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.METALS_COMMON.get(), 2, NiterRegistry.GEMS_COMMON.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.METALS_RARE.get(), 2, NiterRegistry.GEMS_RARE.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.METALS_PRECIOUS.get(), 2, NiterRegistry.GEMS_PRECIOUS.get(), 1);

        this.makeNiterToNiterRecipe(NiterRegistry.OTHER_MINERALS_ABUNDANT.get(), 4, NiterRegistry.GEMS_ABUNDANT.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.OTHER_MINERALS_COMMON.get(), 4, NiterRegistry.GEMS_COMMON.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.OTHER_MINERALS_RARE.get(), 4, NiterRegistry.GEMS_RARE.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.OTHER_MINERALS_PRECIOUS.get(), 4, NiterRegistry.GEMS_PRECIOUS.get(), 1);
    }

    private void otherMinerals() {
        //Add conversion from the niter (representing the whole tier) to the single specific sulfurs
        //This enables conversion between tiers by way of digestion
        this.makeNiterToSulfurRecipe(NiterRegistry.OTHER_MINERALS_ABUNDANT.get(), SulfurMappings.otherMineralsAbundant());
        this.makeNiterToSulfurRecipe(NiterRegistry.OTHER_MINERALS_COMMON.get(), SulfurMappings.otherMineralsCommon());
        this.makeNiterToSulfurRecipe(NiterRegistry.OTHER_MINERALS_RARE.get(), SulfurMappings.otherMineralsRare());
        this.makeNiterToSulfurRecipe(NiterRegistry.OTHER_MINERALS_PRECIOUS.get(), SulfurMappings.otherMineralsPrecious());

        //Also allow direct conversion between specific sulfurs of the same tier
        var otherMineralsFromOtherMinerals = List.of(
                Pair.of(SulfurMappings.otherMineralsAbundant(), ItemTagRegistry.ALCHEMICAL_SULFURS_OTHER_MINERALS_ABUNDANT),
                Pair.of(SulfurMappings.otherMineralsCommon(), ItemTagRegistry.ALCHEMICAL_SULFURS_OTHER_MINERALS_COMMON),
                Pair.of(SulfurMappings.otherMineralsRare(), ItemTagRegistry.ALCHEMICAL_SULFURS_OTHER_MINERALS_RARE),
                Pair.of(SulfurMappings.otherMineralsPrecious(), ItemTagRegistry.ALCHEMICAL_SULFURS_OTHER_MINERALS_PRECIOUS)
        );
        this.makeXtoXRecipes(otherMineralsFromOtherMinerals);

        //Further, allow conversion between types
        this.makeNiterToNiterRecipe(NiterRegistry.METALS_ABUNDANT.get(), 1, NiterRegistry.OTHER_MINERALS_ABUNDANT.get(), 2);
        this.makeNiterToNiterRecipe(NiterRegistry.METALS_COMMON.get(), 1, NiterRegistry.OTHER_MINERALS_COMMON.get(), 2);
        this.makeNiterToNiterRecipe(NiterRegistry.METALS_RARE.get(), 1, NiterRegistry.OTHER_MINERALS_RARE.get(), 2);
        this.makeNiterToNiterRecipe(NiterRegistry.METALS_PRECIOUS.get(), 1, NiterRegistry.OTHER_MINERALS_PRECIOUS.get(), 2);

        this.makeNiterToNiterRecipe(NiterRegistry.GEMS_ABUNDANT.get(), 1, NiterRegistry.OTHER_MINERALS_ABUNDANT.get(), 4);
        this.makeNiterToNiterRecipe(NiterRegistry.GEMS_COMMON.get(), 1, NiterRegistry.OTHER_MINERALS_COMMON.get(), 4);
        this.makeNiterToNiterRecipe(NiterRegistry.GEMS_RARE.get(), 1, NiterRegistry.OTHER_MINERALS_RARE.get(), 4);
        this.makeNiterToNiterRecipe(NiterRegistry.GEMS_PRECIOUS.get(), 1, NiterRegistry.OTHER_MINERALS_PRECIOUS.get(), 4);
    }

    private void logs() {
        //Add conversion from the niter (representing the whole tier) to the single specific sulfurs
        //This enables conversion between tiers by way of digestion
        this.makeNiterToSulfurRecipe(NiterRegistry.LOGS_ABUNDANT.get(), SulfurMappings.logsAbundant());

        //Also allow direct conversion between specific sulfurs of the same tier
        var logsFromLogs = List.of(
                Pair.of(SulfurMappings.logsAbundant(), ItemTagRegistry.ALCHEMICAL_SULFURS_LOGS_ABUNDANT)
        );
        this.makeXtoXRecipes(logsFromLogs);

        //Further, allow conversion between types

        //logs should not convert to minerals, we have log->coal furnace recipes to enable that
//        this.makeNiterToNiterRecipe(NiterRegistry.LOGS_ABUNDANT.get(), 2, NiterRegistry.OTHER_MINERALS_ABUNDANT.get(), 1);

        //but the reverse is fine
        this.makeNiterToNiterRecipe(NiterRegistry.OTHER_MINERALS_ABUNDANT.get(), 1, NiterRegistry.LOGS_ABUNDANT.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.METALS_ABUNDANT.get(), 1, NiterRegistry.LOGS_ABUNDANT.get(), 2);
        this.makeNiterToNiterRecipe(NiterRegistry.GEMS_ABUNDANT.get(), 1, NiterRegistry.LOGS_ABUNDANT.get(), 4);
    }

    private void crops() {
        //Add conversion from the niter (representing the whole tier) to the single specific sulfurs
        //This enables conversion between tiers by way of digestion
        this.makeNiterToSulfurRecipe(NiterRegistry.CROPS_ABUNDANT.get(), SulfurMappings.cropsAbundant());

        //Also allow direct conversion between specific sulfurs of the same tier
        var cropsFromCrops = List.of(
                Pair.of(SulfurMappings.cropsAbundant(), ItemTagRegistry.ALCHEMICAL_SULFURS_CROPS_ABUNDANT)
        );
        this.makeXtoXRecipes(cropsFromCrops);

        //Further, allow conversion between types
        this.makeNiterToNiterRecipe(NiterRegistry.LOGS_ABUNDANT.get(), 2, NiterRegistry.CROPS_ABUNDANT.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.CROPS_ABUNDANT.get(), 2, NiterRegistry.LOGS_ABUNDANT.get(), 1);

        //crops should not convert to minerals, we have crop->log reformation, and log->coal furnace recipes to enable that
//        this.makeNiterToNiterRecipe(NiterRegistry.CROPS_ABUNDANT.get(), 2, NiterRegistry.OTHER_MINERALS_ABUNDANT.get(), 1);

        //minerals to crops is fine though
        this.makeNiterToNiterRecipe(NiterRegistry.OTHER_MINERALS_ABUNDANT.get(), 1, NiterRegistry.CROPS_ABUNDANT.get(), 1);
        this.makeNiterToNiterRecipe(NiterRegistry.METALS_ABUNDANT.get(), 1, NiterRegistry.CROPS_ABUNDANT.get(), 2);
        this.makeNiterToNiterRecipe(NiterRegistry.GEMS_ABUNDANT.get(), 1, NiterRegistry.CROPS_ABUNDANT.get(), 4);
    }

    @Override
    public void buildRecipes(BiConsumer<ResourceLocation, JsonObject> recipeConsumer) {

        //Set up materials that should not get the automatic conversion rates
        this.noAutomaticRecipesFor = Set.of(
                SulfurRegistry.ALLTHEMODIUM.get(),
                SulfurRegistry.UNOBTAINIUM.get(),
                SulfurRegistry.VIBRANIUM.get()
        );

        this.metals();
        this.gems();
        this.otherMinerals();
        this.logs();
        this.crops();

        //now flush cache.
        this.recipeCache.forEach(recipeConsumer);
    }

    public void makeTagRecipe(Item result, TagKey<Item> source, int mercuryFlux) {
        this.makeTagRecipe(result, 1, source, mercuryFlux);
    }

    public void makeTagRecipe(Item result, int resultCount, TagKey<Item> source, int mercuryFlux) {
        this.makeTagRecipe(this.name(result) + "_from_" + this.name(source), result, resultCount, List.of(source), mercuryFlux, TIME);
    }

    public void makeTagRecipe(Item result, List<TagKey<Item>> sources, int mercuryFlux) {
        this.makeTagRecipe(result, 1, sources, mercuryFlux);
    }

    public void makeTagRecipe(Item result, int resultCount, List<TagKey<Item>> sources, int mercuryFlux) {
        this.makeTagRecipe(this.name(result) + "_from_" + this.name(sources), result, resultCount, sources, mercuryFlux, TIME);
    }


    public void makeTagRecipe(String recipeName, Item result, int resultCount, List<TagKey<Item>> sources, int mercuryFlux, int reformationTime) {

        var recipe = new Builder(new ItemStack(result, resultCount))
                .target(result)
                .mercuryFlux(mercuryFlux)
                .time(reformationTime);

        sources.forEach(s -> recipe.sources(s, 1));

        this.recipeCache.put(this.modLoc(recipeName), recipe.build());
    }

    public void makeRecipe(Item result, Item source, int mercuryFlux) {
        this.makeRecipe(result, 1, source, 1, mercuryFlux);
    }

    public void makeRecipe(Item result, int resultCount, Item source, int sourceCount, int mercuryFlux) {
        this.makeRecipe(this.name(result) + "_from_" + this.name(source), result, resultCount, Collections.nCopies(sourceCount, source).stream().toList(), mercuryFlux, TIME);
    }

    public void makeRecipe(String recipeName, Item result, int resultCount, List<Item> sources, int mercuryFlux, int reformationTime) {

        var recipe = new Builder(new ItemStack(result, resultCount))
                .target(result)
                .mercuryFlux(mercuryFlux)
                .time(reformationTime);

        sources.forEach(recipe::sources);

        this.recipeCache.put(this.modLoc(recipeName), recipe.build());
    }

    @Override
    public @NotNull String getName() {
        return "Reformation Recipes";
    }


    protected static class Builder extends RecipeBuilder<Builder> {
        protected Builder(ItemStack result) {
            super(RecipeTypeRegistry.REFORMATION);
            this.result(result);
            this.time(TIME);
        }

        @Override
        public Builder result(ItemStack result) {
            if(result.getItem() instanceof AlchemicalSulfurItem sulfur) {
                if(result.has(DataComponentRegistry.SOURCE_TAG)){
                    var sourceTag = result.get(DataComponentRegistry.SOURCE_TAG);
                    this.condition(new NotCondition(new TagEmptyCondition(sourceTag)));
                }
            }

            return super.result(result);
        }

        public Builder target(Item item) {
            if(item instanceof AlchemicalSulfurItem sulfur) {
                var stack = new ItemStack(sulfur);
                if(stack.has(DataComponentRegistry.SOURCE_TAG)){
                    var sourceTag = stack.get(DataComponentRegistry.SOURCE_TAG);
                    this.condition(new NotCondition(new TagEmptyCondition(sourceTag)));
                }
            }

            return this.ingredient("target", item);
        }

        public Builder mercuryFlux(int mercuryFlux) {
            this.recipe.addProperty("mercuryFlux", mercuryFlux);
            return this.getThis();
        }

        public Builder sources(ItemLike item) {
            //noinspection deprecation
            return this.sources(item.asItem().builtInRegistryHolder());
        }

        public Builder sources(ItemLike item, int count) {
            //noinspection deprecation
            return this.sources(item.asItem().builtInRegistryHolder(), count);
        }

        public Builder sources(Holder<Item> itemHolder) {
            return this.sources(itemHolder, 1);
        }

        public Builder sources(Holder<Item> itemHolder, int count) {
            if (!this.recipe.has("sources"))
                this.recipe.add("sources", new JsonArray());

            JsonObject jsonobject = new JsonObject();
            //noinspection OptionalGetWithoutIsPresent
            jsonobject.addProperty("item", itemHolder.unwrapKey().get().location().toString());
            jsonobject.addProperty("count", count);

            this.recipe.getAsJsonArray("sources").add(jsonobject);

            if(itemHolder.value() instanceof AlchemicalSulfurItem sulfur) {
                var stack = new ItemStack(sulfur);
                if(stack.has(DataComponentRegistry.SOURCE_TAG)){
                    var sourceTag = stack.get(DataComponentRegistry.SOURCE_TAG);
                    this.condition(new NotCondition(new TagEmptyCondition(sourceTag)));
                }
            }

            return this.getThis();
        }


        public Builder sources(TagKey<?> tag, int count) {
            if (!this.recipe.has("sources"))
                this.recipe.add("sources", new JsonArray());

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", tag.location().toString());
            if (count > -1)
                jsonobject.addProperty("count", count);

            this.recipe.getAsJsonArray("sources").add(jsonobject);

            this.condition(new NotCondition(new TagEmptyCondition(tag.location().toString())));

            return this.getThis();
        }
    }
}
