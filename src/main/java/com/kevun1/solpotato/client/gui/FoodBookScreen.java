package com.kevun1.solpotato.client.gui;

import com.kevun1.solpotato.SOLPotato;
import com.kevun1.solpotato.SOLPotatoConfig;
import com.kevun1.solpotato.client.gui.elements.*;
import com.kevun1.solpotato.tracking.FoodInstance;
import com.kevun1.solpotato.tracking.FoodList;
import com.kevun1.solpotato.tracking.benefits.BenefitInfo;
import com.kevun1.solpotato.tracking.benefits.BenefitsHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.kevun1.solpotato.lib.Localization.localized;

@OnlyIn(Dist.CLIENT)
public final class FoodBookScreen extends Screen implements PageFlipButton.Pageable {
	private static final ResourceLocation texture = SOLPotato.resourceLocation("textures/gui/food_book.png");
	private static final UIImage.Image bookImage = new UIImage.Image(texture, new Rectangle(0, 0, 186, 192));
	static final UIImage.Image carrotImage = new UIImage.Image(texture, new Rectangle(0, 240, 16, 16));
	static final UIImage.Image spiderEyeImage = new UIImage.Image(texture, new Rectangle(16, 240, 16, 16));
	static final UIImage.Image heartImage = new UIImage.Image(texture, new Rectangle(0, 224, 15, 15));
	static final UIImage.Image drumstickImage = new UIImage.Image(texture, new Rectangle(16, 224, 15, 15));
	static final UIImage.Image blacklistImage = new UIImage.Image(texture, new Rectangle(32, 224, 15, 15));
	static final UIImage.Image whitelistImage = new UIImage.Image(texture, new Rectangle(48, 224, 15, 15));
	
	static final Color fullBlack = Color.BLACK;
	static final Color lessBlack = new Color(0, 0, 0, 128);
	static final Color leastBlack = new Color(0, 0, 0, 64);
	static final Color activeGreen = new Color(29, 104, 29, 255);
	static final Color inactiveRed = new Color(104, 29, 29, 255);
	
	private final List<UIElement> elements = new ArrayList<>();
	private UIImage background;
	private UILabel pageNumberLabel;
	
	private PageFlipButton nextPageButton;
	private PageFlipButton prevPageButton;
	
	private PlayerEntity player;
	private Set<Map.Entry<FoodInstance, Integer>> foodData;
	
	private final List<Page> pages = new ArrayList<>();
	private int currentPageNumber = 0;
	
	public static void open(PlayerEntity player) {
		Minecraft.getInstance().displayGuiScreen(new FoodBookScreen(player));
	}
	
	public FoodBookScreen(PlayerEntity player) {
		super(new StringTextComponent(""));
		this.player = player;
	}
	
	@Override
	public void init() {
		super.init();
		
		foodData = FoodList.get(player).getData();
		
		background = new UIImage(bookImage);
		background.setCenterX(width / 2);
		background.setCenterY(height / 2);
		
		elements.clear();
		
		// page number
		pageNumberLabel = new UILabel("1");
		pageNumberLabel.setCenterX(background.getCenterX());
		pageNumberLabel.setMinY(background.getMinY() + 156);
		elements.add(pageNumberLabel);
		
		initPages();
		
		int pageFlipButtonSpacing = 50;
		prevPageButton = addButton(new PageFlipButton(
			background.getCenterX() - pageFlipButtonSpacing / 2 - PageFlipButton.width,
			background.getMinY() + 152,
			PageFlipButton.Direction.BACKWARD,
			this
		));
		nextPageButton = addButton(new PageFlipButton(
			background.getCenterX() + pageFlipButtonSpacing / 2,
			background.getMinY() + 152,
			PageFlipButton.Direction.FORWARD,
			this
		));
		
		updateButtonVisibility();
	}
	
	private void initPages() {
		pages.clear();

		double foodDiversity = FoodList.foodDiversity(foodData);
		pages.add(new DiversityPage(foodDiversity, background.frame));

		List<Map.Entry<FoodInstance, Integer>> dataList = new ArrayList<>(foodData);
		dataList.sort(Map.Entry.comparingByValue());
		List<Item> foods = dataList.stream().map(Map.Entry::getKey).map(FoodInstance::getItem).collect(Collectors.toList());
		addPages("food_queue_label", foods);

		Pair<List<BenefitInfo>, List<BenefitInfo>> benefits = BenefitsHandler.getBenefitInfo(foodDiversity);
		List<BenefitInfo> activeBenefits = benefits.getKey();
		List<BenefitInfo> inactiveBenefits = benefits.getValue();
		
		addPages("active_benefits_header", activeBenefits, activeGreen);

		if (SOLPotatoConfig.shouldShowInactiveBenefits()) {
			addPages("inactive_benefits_header", inactiveBenefits, inactiveRed);
		}
	}
	
	private void addPages(String headerLocalizationPath, List<BenefitInfo> benefitInfoList, Color activeColor) {
		String header = localized("gui", "food_book." + headerLocalizationPath);

		pages.addAll(BenefitsPage.pages(background.frame, header, benefitInfoList, activeColor));
	}

	private void addPages(String headerLocalizationPath, List<Item> items) {
		String header = localized("gui", "food_book." + headerLocalizationPath, items.size());
		List<ItemStack> stacks = items.stream().map(ItemStack::new).collect(Collectors.toList());
		Map<FoodInstance, Integer> foodMap = new HashMap<>();
		for (Map.Entry<FoodInstance, Integer> entry : foodData) {
			foodMap.put(entry.getKey(), entry.getValue());
		}

		pages.addAll(FoodListPage.pages(background.frame, header, stacks, foodMap));
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrices);
		
		UIElement.render(matrices, background, mouseX, mouseY);
		
		super.render(matrices, mouseX, mouseY, partialTicks);
		
		if (!pages.isEmpty()) { // might not be loaded yet; race condition
			// current page
			UIElement.render(matrices, elements, mouseX, mouseY);
			UIElement.render(matrices, pages.get(currentPageNumber), mouseX, mouseY);
		}
	}
	
	@Override
	public void switchToPage(int pageNumber) {
		if (!isWithinRange(pageNumber)) return;
		
		currentPageNumber = pageNumber;
		updateButtonVisibility();
		
		pageNumberLabel.text = "" + (currentPageNumber + 1);
	}
	
	@Override
	public int getCurrentPageNumber() {
		return currentPageNumber;
	}
	
	@Override
	public boolean isWithinRange(int pageNumber) {
		return pageNumber >= 0 && pageNumber < pages.size();
	}
	
	private void updateButtonVisibility() {
		prevPageButton.updateState();
		nextPageButton.updateState();
	}
}
