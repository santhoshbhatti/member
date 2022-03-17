package com.infinite.member.config;

import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.infinite.member.messagehandlers.WebsocketMessageListener;

import lombok.extern.slf4j.Slf4j;



@Configuration
@Slf4j
public class RedisConfig {
	
	@Value("${chat.server.host}")
	private String cacheServerIPs;
	
	@Value("${chat.server.password.enabled}")
	private String cacheServerPasswordEnabled;
	
	@Value("${chat.server.password}")
	private String serverPassword;
	
	@Value("${chat.server.mode}")
	private String cacheServerMode;
	
	private Config config = new Config();
	
	private String sentinel = "Sentinel";

	private String cluster = "Cluster";

	private String single = "Single"; 
	
	private String replicated = "Replicated";
	
	private static boolean isRedisConnectionAvailable = false;
	
	@Bean
	public RedissonClient redissonClient() {
			log.info("init redis");
			RedissonClient redisson = null;
			String[] servers = cacheServerIPs.split(",");
			String pswd = cacheServerPasswordEnabled != null ? cacheServerPasswordEnabled.equalsIgnoreCase("TRUE")
					? StringUtils.isEmpty(serverPassword) ? null : serverPassword
							: null : null;
			try {
				int i = 0;
				// To connect sentinal redis mode (for using multi servers new server will be used if current server fails)
				if (sentinel.equalsIgnoreCase(cacheServerMode)) {

					do {
						log.info("init Sentinel");
						config.useSentinelServers().setMasterName("mymaster")
						.addSentinelAddress(
								new String[] { (new StringBuilder("redis://")).append(servers[i]).toString() })
						.setPassword(pswd);
						log.info("init Sentinel success");
						isRedisConnectionAvailable = true;
						i++;
					} while (i < servers.length);

					// To connect Cluster redis mode (for using multi servers all at same time more efficient but expensive)
				} else if (cluster.equalsIgnoreCase(cacheServerMode)) {
					
					for(String node:servers) {
						config.useClusterServers().addNodeAddress("redis://" + node).setPassword(pswd);
						log.info("Enabled cacheClusterServerEnabled:"+node);
					}
					isRedisConnectionAvailable = true;

					// To connect Single redis mode (for using only one server connection)
				} else if (single.equalsIgnoreCase(cacheServerMode)) {

					log.info("init Single");
					config.useSingleServer().setAddress((new StringBuilder("redis://")).append(servers[0]).toString())
					.setPassword(pswd);
					log.info("init Single success");
					isRedisConnectionAvailable = true;
					//Default connection to Master slave modes
				} else if (replicated.equalsIgnoreCase(cacheServerMode)) {
                    log.info("init Replicated Mode");
                    this.config.useReplicatedServers().setScanInterval(2000).setReadMode(ReadMode.MASTER_SLAVE);
                    for (String node : servers) {
                      this.config.useReplicatedServers().addNodeAddress(new String[] { "redis://" + node }).setPassword(pswd);
                      log.info("Enabled cacheClusterServerEnabled:" + node);
                    }
                    log.info("init Replicated success");
                    isRedisConnectionAvailable = true;
                }
				else {

					do {
						log.info("trying to connect master/slave");
						config.useMasterSlaveServers()
						.setMasterAddress((new StringBuilder("redis://")).append(servers[i]).toString())
						.setPassword(pswd);
						log.info("init  master/slave success");
						isRedisConnectionAvailable = true;
						i++;
					} while (i < servers.length);
				}
				redisson = Redisson.create(config);
			} catch (Exception e) {
				log.error("error getting redis connection",e);
			}
			
			if(redisson == null) {
				isRedisConnectionAvailable = false;
				throw new RuntimeException("failed to establish ");
			}
			return redisson;
		
	}
	@Bean
	public RTopic<String> redisTopic() {
	
		 return redissonClient().getTopic("websocketMessageChannel",new StringCodec());
	}
	@Bean
	public WebsocketMessageListener websocketMessageListener() {
		WebsocketMessageListener messageListener = new WebsocketMessageListener();
		redisTopic().addListener(messageListener);
		return messageListener;
	}

}
