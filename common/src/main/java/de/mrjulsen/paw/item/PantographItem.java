package de.mrjulsen.paw.item;

import de.mrjulsen.paw.PantographsAndWires;
import de.mrjulsen.paw.blockentity.PantographBlockEntity;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class PantographItem extends BlockItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private static final RawAnimation ANIM_WIRE_CONTACT = RawAnimation.begin().thenPlayAndHold("wire_contact");

	private final boolean expanded;

	protected PantographItem(Block block, Properties properties, boolean expanded) {
		super(block, properties
			.stacksTo(1)
		);
		this.expanded = expanded;
	}

	public static PantographItem create(Block block, Properties properties, boolean expanded) {
		return new PantographItem(block, properties, expanded);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "popup_controller", 0, state -> {
			MolangParser.INSTANCE.setValue("query.height_percentage", () -> {
				return expanded ? 1D / PantographBlockEntity.DELTA_HEIGHT * 2 : 0;
			});
			MolangParser.INSTANCE.setValue("query.func", () -> {            
				double p = MolangParser.INSTANCE.getVariable("query.height_percentage").get();
				return PantographBlockEntity.getArmAngle(p);
			});
			MolangParser.INSTANCE.setMemoizedValue("query.head_rotation", () -> {
				return 0;
			});
			state.setAnimation(ANIM_WIRE_CONTACT);
			return expanded ? PlayState.CONTINUE : PlayState.STOP;
		}));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<PantographItem> renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeoItemRenderer<>(new DefaultedBlockGeoModel<>(new ResourceLocation(PantographsAndWires.MOD_ID, "pantograph")));

                return this.renderer;
            }
        });
    }
}
