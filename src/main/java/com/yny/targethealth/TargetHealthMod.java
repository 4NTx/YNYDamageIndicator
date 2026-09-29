package com.yny.targethealth;

import dev.xavier.stein.loader.api.Hud;
import dev.xavier.stein.loader.api.Option;
import dev.xavier.stein.loader.api.Page;
import dev.xavier.stein.loader.api.SteinMod;

public final class TargetHealthMod implements SteinMod {

    static final String ID = "ynydamageindicator";
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
            .option(Option.toggle("Show numeric health", () -> settings.showNumbers, value -> {
                settings.showNumbers = value;
                Settings.save(settings);
            }))
            .option(Option.toggle("Show absorption hearts", () -> settings.showAbsorption, value -> {
                settings.showAbsorption = value;
                Settings.save(settings);
            }))
            .restore(() -> {
                settings = new Settings();
                Settings.save(settings);
            });
    }
}
