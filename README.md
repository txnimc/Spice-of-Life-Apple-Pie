<p align="center">
	<img width=256px src="GitHub/logo.png" />
</p>
A mod designed to encourage dietary variety! This project is a fork of Spice of Life: Potato Edition, which itself was a fork of Spice of Life: Carrot Edition. Potato and Carrot Edition share the same goal of rewarding the player for eating a variety of food. However, there are two main differences:  


1. Rather than permanently giving the player max health when they eat a new food, Potato Edition gives variable benefits depending on how diverse the player's *current* diet is. So if the player receives benefits for eating a bunch of new foods, they have to **maintain** that level of diversity to keep those benefits. This is to encourage automation of a variety of food production systems, rather than encouraging the player to eat each food once. 
2. The benefits for dietary diversity are completely customizable and not just limited to max health. Benefits can be modifiers of any attribute like max health, attack damage, armor, movement speed, etc., or any potion effect, from vanilla or any mod. The thresholds for acquiring benefits, the exact calculation for food diversity, how much any individual food contributes to diversity, and more are all configurable.

Basically, instead of using the carrot, it used the potato! Whatever that means...

Sweet Potato Edition aims to port Potato Edition to Minecraft 1.18, and eventually also include functionality from the original Spice of Life, plus maybe some of my own additions. Sweet Potato (or Potato on earlier Minecraft versions), Carrot, and the original Spice of Life are all compatible with each other and offer different incentive structures.

[Example config here](https://github.com/tarinoita/Spice-of-Life-Sweet-Potato/blob/1.18/GitHub/solsweetpotato-server.toml)

Note that there are no dependencies for this mod, but [AttributeFix](https://www.curseforge.com/minecraft/mc-mods/attributefix) is necessary if you want certain attributes to go above the default maximum limit.

### Features

This mod keeps track of the past 32 (configurable) foods you've eaten, and assigns a diversity score based on how diverse those foods are. The greater the variety of foods you've eaten, the higher your diversity score is, with a greater weight on recently eaten foods. You are then given various benefits depending on how high your current diversity score is. 

Note that this means your diversity score is constantly and dynamically changing depending on what your current and past diet looks like. Thus, you can have a lot of benefits after eating a bunch of different foods, but if you go back to eating only bread, you will gradually lose all of your benefits. To get higher diversity scores, you need to keep including more and more different types of foods in your diet.

The exact computation of the diversity score is fairly involved, but I've included a description in the [wiki](https://github.com/tarinoita/Spice-of-Life-Sweet-Potato-Edition/wiki). The method of computation is also very configurable, along with what benefits the player receives and at what diversity threshold they will receive those benefits. Please read the wiki to see exactly what the config does and how to change it.

By default, this mod gives you more max health the more diverse your diet is. Also by default, for increasingly high levels of diversity, you can receive permanent speed, strength, regeneration, and armor toughness bonuses (as long as you maintain that diverse diet). 

The ceiling for maxing out your diversity is very high (and can be configured to be even higher), so if there are tons of different foods from various mods, such as HarvestCraft, there will **always be an incentive to automate every type of food**, no matter how much you have already automated.  No more having a set of staple foods and never having any incentive to branch out! No more making a complex dish or hunting exotic game only to realize you don't have any incentive to make it ever again! 

### Items

**Food Book** - The food book gives you all the information you need to know, including your current diversity score, all of the foods you've eaten in the past that are currently contributing to your diversity, and what benefits you have unlocked and can unlock. The food book GUI can also be accessed with a hotkey (unbound by default), if you don't want to carry around a physical item.

**Lunchbag and Lunchbox** - That's right, the lunchbag and lunchbox from the original Spice of Life are back! They function similarly, by holding food items and automatically selecting the food that will give you the maximum diversity increase (or the minimum diversity loss). 

### Commands

This mod also features 3 commands:

- /solsweetpotato diversity  
    Tells you your current diversity score. The score is easily accessed with the Food Book, but this offers another convenient way to do it.
- /solsweetpotato clear  
    Clears the stored list of foods a player has eaten and resets their benefits. This is useful for testing when editing the config or when you want to start over.
- /solsweetpotato sync  
    Forces a sync of the food list to the client, for when something went wrong and it's mismatched.

### FAQs

- Will you port to another version/Fabric?
  - There are no plans for anything other than 1.18 Forge at the moment.
- How do I make custom benefits?
  - Read the config, then read the wiki if you are still unsure.
- Where is the server config located?
  - In newer versions of Forge (post MC 1.12), server configs are generated per world in `saves/[world name]/serverconfig`. They are NOT in the regular `config` folder, which only contains client configs. 
- Is it possible to have penalties for low diversity, in addition to benefits for high diversity?
  - There is no built in support for penalties, but yes, the config is powerful enough to make benefits into penalties. You can make benefits with negative attribute modifiers or negative potion effects that act as penalties. The philosophy of the mod is still in line with Carrot edition, though, meaning that food diversity should be encouraged with benefits, not punishments.
- Help! Something isn't working!
  - If you use wrong syntax in the config, stuff will not work. If you haven't touched your config/can't figure what's wrong, please report issues [here](https://github.com/tarinoita/Spice-of-Life-Sweet-Potato-Edition/issues).
- The Lunchbag and Lunchbox don't work with AppleSkin!
  - This is a known issue. Because AppleSkin no longer has an API 1.14+, I can't make it read the food values from the Lunchbox, since it's not a regular food item, unless I'm missing something.
- How do I contact you in general?
  - Leave comments on CurseForge. If you find me on Discord, you can shoot me a message, and I'll probably get around to setting up a server there someday.

### Credits

- Many thanks to Kevun1 for creating the original [Spice of Life: Potato Edition](https://github.com/Kevun1/Spice-of-Life-Potato-Edition), my personal favorite Spice of Life variant and a must-have for any of my servers, alongside Pam's Harvestcraft or Farmer's Delight.
- Thanks also to Cazsius and the rest of the team that worked on [Spice of Life: Carrot Edition](https://github.com/Cazsius/Spice-of-Life-Carrot-Edition), which the original Potato Edition was forked from.


