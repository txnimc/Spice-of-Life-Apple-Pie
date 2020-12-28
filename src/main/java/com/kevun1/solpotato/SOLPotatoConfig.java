package com.kevun1.solpotato;

import com.kevun1.solpotato.tracking.CapabilityHandler;
import com.kevun1.solpotato.tracking.FoodInstance;
import com.kevun1.solpotato.tracking.benefits.Benefit;
import com.kevun1.solpotato.tracking.benefits.BenefitsHandler;
import com.kevun1.solpotato.utils.BenefitsParser;
import com.google.common.collect.Lists;
import com.kevun1.solpotato.utils.ComplexityParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.regex.Pattern;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@Mod.EventBusSubscriber(modid = SOLPotato.MOD_ID, bus = MOD)
public final class SOLPotatoConfig {
	private static String localizationPath(String path) {
		return "config." + SOLPotato.MOD_ID + "." + path;
	}
	
	public static final Server SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	static {
		Pair<Server, ForgeConfigSpec> specPair = new Builder().configure(Server::new);
		SERVER = specPair.getLeft();
		SERVER_SPEC = specPair.getRight();

		SERVER.benefitsList = BenefitsParser.parse(getBenefitsUnparsed());

	}
	
	public static final Client CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;
	
	static {
		Pair<Client, ForgeConfigSpec> specPair = new Builder().configure(Client::new);
		CLIENT = specPair.getLeft();
		CLIENT_SPEC = specPair.getRight();
	}
	
	public static void setUp() {
		ModLoadingContext context = ModLoadingContext.get();
		context.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
		context.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
	}
	
	@SubscribeEvent
	public static void onConfigReload(ModConfig.Reloading event) {
		MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
		if (currentServer == null) return;
		
		PlayerList players = currentServer.getPlayerList();
		for (PlayerEntity player : players.getPlayers()) {
			BenefitsHandler.removeAllBenefits(player);
			BenefitsHandler.updatePlayer(player);
			CapabilityHandler.syncFoodList(player);
		}
	}

	@SubscribeEvent
	public static void onConfigLoad(ModConfig.Loading event) {
		/* Very dangerous: If SERVER.benefitsList gets changed without calling BenefitsHandler#removeAllBenefits
		on all players, players will get stuck with a modified attribute forever, since attribute modifiers are
		created with a temporary UUID. removeAllBenefits is called whenever a player logs out, and the only instance
		of updating benefitsList is here, which only gets called on server load. Thus, everything is fine as long
		as parsing only takes place here.
		However, there should be a safer way to handle attribute modifiers.

		TODO: store benefitsList in a manager class, and only parse from config on server load. When updating, should
		call BenefitsHandler#removeAllBenefits on all players on server to be extra safe. Alternatively, store
		attribute modifier UUIDs as part of FoodCapability of players.
		 */

		SERVER.benefitsList = BenefitsParser.parse(getBenefitsUnparsed());
		SERVER.complexityMap = ComplexityParser.parse(getComplexityUnparsed());
	}

	public static List<String> getBlacklist() {
		return new ArrayList<>(SERVER.blacklist.get());
	}
	
	public static List<String> getWhitelist() {
		return new ArrayList<>(SERVER.whitelist.get());
	}

	public static List<String> getBenefitsUnparsed() { return new ArrayList<>(SERVER.benefitsUnparsed.get()); }

	public static List<String> getComplexityUnparsed() {return new ArrayList<>(SERVER.complexityUnparsed.get());}

	public static List<List<Benefit>> getBenefitsList() { return SERVER.benefitsList; }

	public static Map<FoodInstance, Double> getComplexityMap() { return SERVER.complexityMap; }

	public static List<Double> getThresholds() { return new ArrayList<>(SERVER.thresholds.get()); }

	public static boolean shouldResetOnDeath() {
		return SERVER.shouldResetOnDeath.get();
	}
	
	public static boolean limitProgressionToSurvival() {
		return SERVER.limitProgressionToSurvival.get();
	}

	public static boolean shouldForbiddenCount() { return SERVER.shouldForbiddenCount.get(); }

	public static Integer size() {
		return SERVER.queueSize.get();
	}

	public static Integer endDecay() {
		return SERVER.endDecay.get();
	}

	public static Integer startDecay() {
		return SERVER.startDecay.get();
	}

	public static Double minContribution() {
		return SERVER.minContribution.get();
	}

	public static Double defaultContribution() {
		return SERVER.defaultContribution.get();
	}
	
	public static class Server {
		public final ConfigValue<List<? extends String>> blacklist;
		public final ConfigValue<List<? extends String>> whitelist;

		public final IntValue queueSize;
		
		public final BooleanValue shouldResetOnDeath;
		public final BooleanValue limitProgressionToSurvival;

		public final ConfigValue<List<? extends Double>> thresholds;
		public final ConfigValue<List<? extends String>> benefitsUnparsed;

		public List<List<Benefit>> benefitsList;
		public Map<FoodInstance, Double> complexityMap;

		public final DoubleValue minContribution;
		public final DoubleValue defaultContribution;
		public final IntValue endDecay;
		public final IntValue startDecay;

		public final BooleanValue shouldForbiddenCount;

		public final ConfigValue<List<? extends String>> complexityUnparsed;

		Server(Builder builder) {
			builder.push("filtering");
			
			blacklist = builder
				.translation(localizationPath("blacklist"))
				.comment("Foods in this list won't affect the player's health nor show up in the food book.")
				.defineList("blacklist", Lists.newArrayList(), e -> e instanceof String);
			
			whitelist = builder
				.translation(localizationPath("whitelist"))
				.comment("When this list contains anything, the blacklist is ignored and instead only foods from here count.")
				.defineList("whitelist", Lists.newArrayList(), e -> e instanceof String);

			builder.pop();
			builder.push("miscellaneous");
			
			shouldResetOnDeath = builder
				.translation(localizationPath("reset_on_death"))
				.comment("Whether or not to reset the food list on death, effectively losing all bonus hearts.")
				.define("resetOnDeath", false);
			
			limitProgressionToSurvival = builder
				.translation(localizationPath("limit_progression_to_survival"))
				.comment("If true, eating foods outside of survival mode (e.g. creative/adventure) is not tracked and thus does not contribute towards progression.")
				.define("limitProgressionToSurvival", false);

			queueSize = builder
					.translation(localizationPath("queue_size"))
					.comment("How many foods should be tracked. I.e., how many food items eaten in the past should count toward food diversity.")
					.defineInRange("queueSize", 32, 1, 1000);
			
			builder.pop();
			builder.push("benefits");

			thresholds = builder
					.translation(localizationPath("thresholds"))
					.comment("A list of numbers of diversity values, in ascending order.")
					.defineList("thresholds", Lists.newArrayList(1.2, 2.0, 15.0, 20.0, 25.0), e -> e instanceof Double);

			benefitsUnparsed = builder
					.translation(localizationPath("benefits_unparsed"))
					.comment("A list of numbers of diversity values, in ascending order.")
					.defineList("benefitsUnparsed", Lists.newArrayList(
							"attribute,generic.max_health,2;effect,strength,1", "attribute,generic.max_health,2;effect,strength,2"),
							e -> e instanceof String);

			builder.pop();
			builder.push("Advanced");

			minContribution = builder
					.translation(localizationPath("min_contribution"))
					.comment("Lowest possible diversity contribution a food can give.")
					.defineInRange("minContribution", 0.0, 0.0, 1.0);

			defaultContribution = builder
					.translation(localizationPath("default_contribution"))
					.comment("The default diversity value of the most recent food eaten.")
					.defineInRange("defaultContribution", 1.0, 0.0, 100.0);

			endDecay = builder
					.translation(localizationPath("end_decay"))
					.comment("How many meals in the past should the diversity penalty stop from. Needs to be < queue_size and > start_decay")
					.defineInRange("endDecay", 32, 0, 1000);

			startDecay = builder
					.translation(localizationPath("start_decay"))
					.comment("How many meals in the past should the diversity time penalty start to apply. Needs to be < queue_size and <= end_decay.")
					.defineInRange("startDecay", 0, 0, 1000);

			shouldForbiddenCount = builder
					.translation(localizationPath("should_forbidden_count"))
					.comment("Whether blacklisted foods or foods that don't contribute any diversity should still take a spot in the queue.")
					.define("shouldForbiddenCount", true);

			builder.pop();
			builder.push("Complexity");

			complexityUnparsed = builder
					.translation(localizationPath("complexity_unparsed"))
					.comment("Define custom complexity values for individual foods here.")
					.defineList("complexityUnparsed", Lists.newArrayList(
							"minecraft:golden_carrot,2", "minecraft:golden_carrot,2"),
							e -> e instanceof String);


		}
	}

	public static boolean isFoodTooltipEnabled() {
		return CLIENT.isFoodTooltipEnabled.get();
	}
	
	public static boolean shouldShowProgressAboveHotbar() {
		return CLIENT.shouldShowProgressAboveHotbar.get();
	}
	
	public static boolean shouldShowUneatenFoods() {
		return CLIENT.shouldShowUneatenFoods.get();
	}
	
	public static class Client {
		public final BooleanValue isFoodTooltipEnabled;
		public final BooleanValue shouldShowProgressAboveHotbar;
		public final BooleanValue shouldShowUneatenFoods;
		
		Client(Builder builder) {
			builder.push("miscellaneous");
			
			isFoodTooltipEnabled = builder
				.translation(localizationPath("is_food_tooltip_enabled"))
				.comment("If true, foods indicate in their tooltips whether or not they have been eaten.")
				.define("isFoodTooltipEnabled", true);
			
			shouldShowProgressAboveHotbar = builder
				.translation(localizationPath("should_show_progress_above_hotbar"))
				.comment("Whether the messages notifying you of reaching new milestones should be displayed above the hotbar or in chat.")
				.define("shouldShowProgressAboveHotbar", true);
			
			shouldShowUneatenFoods = builder
				.translation(localizationPath("should_show_uneaten_foods"))
				.comment("If true, the food book also lists foods that you haven't eaten, in addition to the ones you have.")
				.define("shouldShowUneatenFoods", true);
			
			builder.pop();
		}
	}
	
	// TODO: investigate performance of all these get() calls
	
	public static boolean hasWhitelist() {
		return !SERVER.whitelist.get().isEmpty();
	}
	
	public static boolean isAllowed(Item food) {
		String id = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(food)).toString();
		if (hasWhitelist()) {
			return matchesAnyPattern(id, SERVER.whitelist.get());
		} else {
			return !matchesAnyPattern(id, SERVER.blacklist.get());
		}
	}
	
	public static boolean shouldCount(Item food) {
		return isAllowed(food);
	}

	private static boolean matchesAnyPattern(String query, Collection<? extends String> patterns) {
		for (String glob : patterns) {
			StringBuilder pattern = new StringBuilder(glob.length());
			for (String part : glob.split("\\*", -1)) {
				if (!part.isEmpty()) { // not necessary
					pattern.append(Pattern.quote(part));
				}
				pattern.append(".*");
			}
			
			// delete extraneous trailing ".*" wildcard
			pattern.delete(pattern.length() - 2, pattern.length());
			
			if (Pattern.matches(pattern.toString(), query)) {
				return true;
			}
		}
		return false;
	}
}
