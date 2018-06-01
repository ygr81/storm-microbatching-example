package net.vault7;

import lombok.extern.slf4j.Slf4j;
import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseTickTupleAwareRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Slf4j
class TickTupleBatchBolt extends BaseTickTupleAwareRichBolt {

  private static final int BATCH_SIZE = 1000;
  private static final long BATCH_INTERVAL_IN_SECS = 1;

  private transient OutputCollector collector;
  private transient MongoDbService mongoDbService;
  private LinkedBlockingQueue<Tuple> queue;

  private long lastBatchProcessTimeSeconds;

  private static long currentTimeInSecs() {
    return System.currentTimeMillis() / 1_000;
  }

  @Override
  protected void process(Tuple tuple) {
    queue.add(tuple);
    int queueSize = queue.size();
    if (queueSize >= BATCH_SIZE) {
      finishBatch();
    }
  }

  @Override
  protected void onTickTuple(final Tuple tuple) {
    if (shouldFlush()) {
      finishBatch();
    }
  }

  private void finishBatch() {
    lastBatchProcessTimeSeconds = currentTimeInSecs();

    if (queue.isEmpty()) {
      return;
    }

    List<Tuple> tuples = new ArrayList<>();
    queue.drainTo(tuples);

    mongoDbService.insertAdresses(
        tuples.stream().map(t -> (Address) t.getValueByField(OutputFields.ADDRESS))
            .collect(Collectors.toList()));

    for (Tuple tuple : tuples) {
      collector.ack(tuple);
    }

    log.info("{} tuples acked.", tuples.size() );
  }

  private boolean shouldFlush() {
    return (currentTimeInSecs() - lastBatchProcessTimeSeconds) >= BATCH_INTERVAL_IN_SECS;
  }

  @Override
  public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    this.collector = collector;
    mongoDbService = new MongoDbService();
    queue = new LinkedBlockingQueue<>();
  }


  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    // no further processing expected
  }


  @Override
  public Map<String, Object> getComponentConfiguration() {
    Config config = new Config();
    config.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 1);
    return config;
  }
}
