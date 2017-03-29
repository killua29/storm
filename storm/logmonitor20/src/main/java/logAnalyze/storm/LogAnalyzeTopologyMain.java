package logAnalyze.storm;


import logAnalyze.storm.bolt.MessageFilterBolt;
import logAnalyze.storm.bolt.ProcessMessage;
import logAnalyze.storm.spout.RandomSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

/**
 * Describe: 请补充类描述
 * Author:   maoxiangyi
 * Domain:   www.itcast.cn
 * Data:     2015/11/16.
 */
public class LogAnalyzeTopologyMain {
    public static void main(String[] args) throws  Exception{
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kafka-spout", new RandomSpout(), 2);
        builder.setBolt("MessageFilter-bolt",new MessageFilterBolt(),3).shuffleGrouping("kafka-spout");
        builder.setBolt("ProcessMessage-bolt",new ProcessMessage(),2).fieldsGrouping("MessageFilter-bolt", new Fields("type"));
        Config topologConf = new Config();
        if (args != null && args.length > 0) {
            topologConf.setNumWorkers(2);
            StormSubmitter.submitTopologyWithProgressBar(args[0], topologConf, builder.createTopology());
        } else {
            topologConf.setMaxTaskParallelism(3);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("LogAnalyzeTopologyMain", topologConf, builder.createTopology());
            Utils.sleep(10000000);
            cluster.shutdown();
        }
    }
}