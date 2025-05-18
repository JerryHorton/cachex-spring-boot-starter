package cn.cug.sxy.cachex.sync.listener;

import cn.cug.sxy.cachex.sync.canal.CanalRedisSyncRunner;
import cn.cug.sxy.cachex.sync.config.CanalClientProperties;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/5/16 22:30
 * @Description 监听者
 * @Author jerryhotton
 */

@Slf4j
public class CanalClientListener {

    private final CanalRedisSyncRunner syncRunner;

    private final CanalClientProperties properties;

    public CanalClientListener(CanalRedisSyncRunner syncRunner, CanalClientProperties properties) {
        this.syncRunner = syncRunner;
        this.properties = properties;
    }

    @PostConstruct
    public void start() {
        new Thread(this::listen).start();
    }

    private void listen() {
        while (true) {
            CanalConnector connector = CanalConnectors.newSingleConnector(
                    new InetSocketAddress(properties.getHost(), properties.getPort()),
                    properties.getDestination(),
                    properties.getUsername(),
                    properties.getPassword());

            try {
                connector.connect();
                connector.subscribe(properties.getSubscribe());
                connector.rollback();
                log.info("✅ Canal Client connected to {}:{}", properties.getHost(), properties.getPort());

                while (true) {
                    Message message = connector.getWithoutAck(properties.getBatchSize());
                    long batchId = message.getId();
                    try {
                        if (-1 == batchId || message.getEntries().isEmpty()) {
                            Thread.sleep(properties.getEmptySleepMs());
                            continue;
                        }
                        for (Entry entry : message.getEntries()) {
                            if (entry.getEntryType() != CanalEntry.EntryType.ROWDATA) {
                                continue;
                            }
                            // 只处理 INSERT / UPDATE / DELETE
                            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                            EventType eventType = rowChange.getEventType();
                            if (eventType != EventType.INSERT && eventType != EventType.UPDATE && eventType != EventType.DELETE) {
                                continue;
                            }
                            String database = entry.getHeader().getSchemaName();
                            String table = entry.getHeader().getTableName();
                            for (RowData rowData : rowChange.getRowDatasList()) {
                                List<Column> columns;
                                switch (eventType) {
                                    case INSERT:
                                    case UPDATE:
                                        columns = rowData.getAfterColumnsList();
                                        syncRunner.onRowChange(database, table, columns);
                                        break;
                                    case DELETE:
                                        columns = rowData.getBeforeColumnsList();
                                        syncRunner.onRowDelete(database, table, columns);
                                        break;
                                    default:
                                        log.warn("不支持的事件类型：{}", eventType);
                                }
                            }
                        }
                        connector.ack(batchId);
                    } catch (Exception e) {
                        log.error("❌ Canal 消息处理失败，回滚中：", e);
                        connector.rollback(batchId);
                    }
                }
            } catch (Exception e) {
                log.error("❌ Canal Client 启动失败，5秒后重试：", e);
            } finally {
                connector.disconnect();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
