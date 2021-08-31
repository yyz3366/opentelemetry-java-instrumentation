/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.oshi;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.GlobalMeterProvider;
import io.opentelemetry.api.metrics.Meter;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

/** System Metrics Utility. */
public class SystemMetrics {
  private static final AttributeKey<String> DEVICE_KEY = AttributeKey.stringKey("device");
  private static final AttributeKey<String> DIRECTION_KEY = AttributeKey.stringKey("direction");

  private static final AttributeKey<String> STATE_KEY = AttributeKey.stringKey("state");

  private static final Attributes ATTRIBUTES_USED = Attributes.of(STATE_KEY, "used");
  private static final Attributes ATTRIBUTES_FREE = Attributes.of(STATE_KEY, "free");

  private SystemMetrics() {}

  /** Register observers for system metrics. */
  public static void registerObservers() {
    Meter meter = GlobalMeterProvider.get().get("io.opentelemetry.instrumentation.oshi");
    SystemInfo systemInfo = new SystemInfo();
    HardwareAbstractionLayer hal = systemInfo.getHardware();

    meter
        .gaugeBuilder("system.memory.usage")
        .ofLongs()
        .setDescription("System memory usage")
        .setUnit("By")
        .buildWithCallback(
            r -> {
              GlobalMemory mem = hal.getMemory();
              r.observe(mem.getTotal() - mem.getAvailable(), ATTRIBUTES_USED);
              r.observe(mem.getAvailable(), ATTRIBUTES_FREE);
            });

    meter
        .gaugeBuilder("system.memory.utilization")
        .setDescription("System memory utilization")
        .setUnit("1")
        .buildWithCallback(
            r -> {
              GlobalMemory mem = hal.getMemory();
              r.observe(
                  ((double) (mem.getTotal() - mem.getAvailable())) / mem.getTotal(),
                  ATTRIBUTES_USED);
              r.observe(((double) mem.getAvailable()) / mem.getTotal(), ATTRIBUTES_FREE);
            });

    meter
        .gaugeBuilder("system.network.io")
        .ofLongs()
        .setDescription("System network IO")
        .setUnit("By")
        .buildWithCallback(
            r -> {
              for (NetworkIF networkIf : hal.getNetworkIFs()) {
                networkIf.updateAttributes();
                long recv = networkIf.getBytesRecv();
                long sent = networkIf.getBytesSent();
                String device = networkIf.getName();
                r.observe(recv, Attributes.of(DEVICE_KEY, device, DIRECTION_KEY, "receive"));
                r.observe(sent, Attributes.of(DEVICE_KEY, device, DIRECTION_KEY, "transmit"));
              }
            });

    meter
        .gaugeBuilder("system.network.packets")
        .ofLongs()
        .setDescription("System network packets")
        .setUnit("packets")
        .buildWithCallback(
            r -> {
              for (NetworkIF networkIf : hal.getNetworkIFs()) {
                networkIf.updateAttributes();
                long recv = networkIf.getPacketsRecv();
                long sent = networkIf.getPacketsSent();
                String device = networkIf.getName();
                r.observe(recv, Attributes.of(DEVICE_KEY, device, DIRECTION_KEY, "receive"));
                r.observe(sent, Attributes.of(DEVICE_KEY, device, DIRECTION_KEY, "transmit"));
              }
            });

    meter
        .gaugeBuilder("system.network.errors")
        .ofLongs()
        .setDescription("System network errors")
        .setUnit("errors")
        .buildWithCallback(
            r -> {
              for (NetworkIF networkIf : hal.getNetworkIFs()) {
                networkIf.updateAttributes();
                long recv = networkIf.getInErrors();
                long sent = networkIf.getOutErrors();
                String device = networkIf.getName();
                r.observe(recv, Attributes.of(DEVICE_KEY, device, DIRECTION_KEY, "receive"));
                r.observe(sent, Attributes.of(DEVICE_KEY, device, DIRECTION_KEY, "transmit"));
              }
            });

    meter
        .gaugeBuilder("system.disk.io")
        .ofLongs()
        .setDescription("System disk IO")
        .setUnit("By")
        .buildWithCallback(
            r -> {
              for (HWDiskStore diskStore : hal.getDiskStores()) {
                long read = diskStore.getReadBytes();
                long write = diskStore.getWriteBytes();
                String device = diskStore.getName();
                r.observe(read, Attributes.of(DEVICE_KEY, device, DIRECTION_KEY, "read"));
                r.observe(write, Attributes.of(DEVICE_KEY, device, DIRECTION_KEY, "write"));
              }
            });

    meter
        .gaugeBuilder("system.disk.operations")
        .ofLongs()
        .setDescription("System disk operations")
        .setUnit("operations")
        .buildWithCallback(
            r -> {
              for (HWDiskStore diskStore : hal.getDiskStores()) {
                long read = diskStore.getReads();
                long write = diskStore.getWrites();
                String device = diskStore.getName();
                r.observe(read, Attributes.of(DEVICE_KEY, device, DIRECTION_KEY, "read"));
                r.observe(write, Attributes.of(DEVICE_KEY, device, DIRECTION_KEY, "write"));
              }
            });
  }
}
