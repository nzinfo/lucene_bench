# lucene_bench
Lucene Benchmark

#### Build:

> mvn clean package

#### Run:

> ./bin/run_idx_bench.sh &lt;path to config file&gt;

#### Configuration:

* **data.dir:** source data file
* **index.dir:** output index directory
* **docs.per.segment:** number of documents to accumulate in memory before flushing to disk
* **rawstore:** whether or not store raw data
* **docbuilder.class:** factory class that converts a json to a Lucene document

See [Example configuration files:](https://github.com/LogInsight/lucene_bench/tree/master/conf)
