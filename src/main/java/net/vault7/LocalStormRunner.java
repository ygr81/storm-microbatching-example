package net.vault7;

import org.apache.storm.Config;
import org.apache.storm.starter.util.StormRunner;
import org.apache.storm.topology.TopologyBuilder;

class LocalStormRunner {

  private static final int DEFAULT_RUNTIME_IN_SECONDS = 30;
  private static final String SPOUT_ID = "micro-batching-spout";
  private static final String BOLT_ID = "micro-batching-bolt";

  private final TopologyBuilder builder;
  private final String topologyName;
  private final Config topologyConfig;


  private LocalStormRunner(String topologyName) {
    this.topologyName = topologyName;
    builder = new TopologyBuilder();
    topologyConfig = createTopologyConfig();
    wireTopology();
  }

  public static void main(String[] args) throws InterruptedException {
    new LocalStormRunner(LocalStormRunner.class.getSimpleName() + "-topology").run();
  }

  private static Config createTopologyConfig() {
    Config config = new Config();
    config.setDebug(false);
    config.setMessageTimeoutSecs(3);
    return config;
  }


  private void run() throws InterruptedException {
    StormRunner.runTopologyLocally(builder.createTopology(), topologyName, topologyConfig,
        DEFAULT_RUNTIME_IN_SECONDS);
  }


  private void wireTopology() {
    builder.setSpout(SPOUT_ID, new AddressSpout(), 1);
    builder.setBolt(BOLT_ID, new TickTupleBatchBolt(), 1).shuffleGrouping(SPOUT_ID);
  }
}
