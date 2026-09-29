package com.yny.targethealth;

import dev.xavier.stein.loader.api.Hud;
import dev.xavier.stein.loader.api.Option;
import dev.xavier.stein.loader.api.Page;
import dev.xavier.stein.loader.api.SteinMod;

public final class TargetHealthMod implements SteinMod {

    static final String ID = "ynydamageindicator";
    private static final String[] RANGES = {"8 blocks", "16 blocks", "32 blocks", "48 blocks", "64 blocks"};
    private static final String[] OPACITIES = {"Low", "Medium", "High", "Solid"};
    static Settings settings = new Settings();

    private final TargetHealthElement element = new TargetHealthElement();

    @Override
    public void afterStartGame() {
        settings = Settings.load();
        Hud.register(element);
    }

    @Override
    public void onTickEnd() {
        element.updateDistantTarget();
    }

    @Override
    public void onPage(Page page) {
        page.title("YNYDamageIndicator")
            .section("Display")
            .option(Option.toggle("Enable indicator", () -> settings.enabled, value -> {
                settings.enabled = value;
                Settings.save(settings);
            }))
            .option(Option.toggle("Show target name", () -> settings.showName, value -> {
                settings.showName = value;
                Settings.save(settings);
            }))
            .option(Option.toggle("Show numeric health", () -> settings.showNumbers, value -> {
                settings.showNumbers = value;
                Settings.save(settings);
            }))
            .option(Option.toggle("Show absorption hearts", () -> settings.showAbsorption, value -> {
                settings.showAbsorption = value;
                Settings.save(settings);
            }))
            .section("Targeting")
            .option(Option.toggle("Show distant targets", () -> settings.showDistantTargets, value -> {
                settings.showDistantTargets = value;
                Settings.save(settings);
            }))
            .option(Option.cycle("Visual range", RANGES, () -> settings.rangeIndex, value -> {
                settings.rangeIndex = value;
                Settings.save(settings);
            }))
            .option(Option.toggle("Ignore leaves", () -> settings.ignoreLeaves, value -> {
                settings.ignoreLeaves = value;
                Settings.save(settings);
            }))
            .section("Appearance")
            .option(Option.color("Accent color", () -> settings.accentColor, value -> {
                settings.accentColor = value;
                Settings.save(settings);
            }))
            .option(Option.toggle("Dynamic health color", () -> settings.dynamicHealthColor, value -> {
                settings.dynamicHealthColor = value;
                Settings.save(settings);
            }))
            .option(Option.color("Health color", () -> settings.healthColor, value -> {
                settings.healthColor = value;
                Settings.save(settings);
            }))
            .option(Option.cycle("Background opacity", OPACITIES, () -> settings.opacityIndex, value -> {
                settings.opacityIndex = value;
                Settings.save(settings);
            }))
            .restore(() -> {
                settings = new Settings();
                Settings.save(settings);
            });
    }
}
