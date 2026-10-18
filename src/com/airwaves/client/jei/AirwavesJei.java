/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.IModPlugin
 *  mezz.jei.api.JeiPlugin
 *  mezz.jei.api.recipe.RecipeType
 *  mezz.jei.api.recipe.category.IRecipeCategory
 *  mezz.jei.api.registration.IRecipeCatalystRegistration
 *  mezz.jei.api.registration.IRecipeCategoryRegistration
 *  mezz.jei.api.registration.IRecipeRegistration
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package com.airwaves.client.jei;

import com.airwaves.ModRegistry;
import com.airwaves.client.jei.BoardRecipeCategory;
import com.airwaves.logic.BoardDesign;
import java.util.ArrayList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class AirwavesJei
implements IModPlugin {
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"jei");
    }

    public void registerCategories(IRecipeCategoryRegistration r) {
        r.addRecipeCategories(new IRecipeCategory[]{new BoardRecipeCategory(r.getJeiHelpers().getGuiHelper())});
    }

    public void registerRecipes(IRecipeRegistration r) {
        ArrayList<BoardRecipeCategory.Recipe> list = new ArrayList<BoardRecipeCategory.Recipe>();
        for (BoardDesign d : BoardDesign.values()) {
            list.add(new BoardRecipeCategory.Recipe(d, d.exampleLayout()));
        }
        r.addRecipes(BoardRecipeCategory.TYPE, list);
    }

    public void registerRecipeCatalysts(IRecipeCatalystRegistration r) {
        r.addRecipeCatalyst(new ItemStack((ItemLike)ModRegistry.CIRCUIT_WORKBENCH_ITEM.get()), new RecipeType[]{BoardRecipeCategory.TYPE});
    }
}
