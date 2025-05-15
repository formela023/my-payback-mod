package com.example.mypaymod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayMod implements ClientModInitializer {

    private final Pattern paymentPattern = Pattern.compile("(?i)(\\w+) Paid You ([\\d.]+)([km]?)");

    @Override
    public void onInitializeClient() {
        ClientReceiveMessageEvents.CHAT.register((message, signed, sender, params, timestamp) -> {
            String rawMessage = message.getString();
            handlePaymentMessage(rawMessage);
            return ActionResult.PASS;
        });
    }

    private void handlePaymentMessage(String message) {
        Matcher matcher = paymentPattern.matcher(message);
        if (matcher.find()) {
            String playerName = matcher.group(1);
            double amount = Double.parseDouble(matcher.group(2));
            String suffix = matcher.group(3).toLowerCase();

            if ("k".equals(suffix)) {
                amount *= 1_000;
            } else if ("m".equals(suffix)) {
                amount *= 1_000_000;
            }

            if (amount >= 100_000 && amount <= 30_000_000) {
                if (Math.random() < 0.3) {
                    int doubled = (int) (amount * 2);
                    sendCommand("/pay " + playerName + " " + doubled);
                    sendCommand("/msg " + playerName + " congrats");
                } else {
                    sendCommand("/msg " + playerName + " im sorry You lost");
                }
            }
        }
    }

    private void sendCommand(String command) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand(command.substring(1));
        }
    }
}
