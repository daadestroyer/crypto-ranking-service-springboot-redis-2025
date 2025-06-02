# crypto-ranking-service-springboot-redis-2025

### 1. This is home page where we can see list of all top 50 Crypto coins
![img.png](img.png)
![img_1.png](img_1.png)
![img_2.png](img_2.png)

### 2. We can choose any coin and can see the Time Series of the price of particular coin
![img_3.png](img_3.png)
![img_4.png](img_4.png)
![img_5.png](img_5.png)

1. Backend Code running at : localhost:1001
2. UI Code running at : localhost:3000

### 3. Create a project

![img_6.png](img_6.png)
![img_7.png](img_7.png)

### 4. Docker commands
###### first time
```
docker run -d --name redis-stack-server -p 6379:6379 -p 8001:8001 redis/redis-stack:latest
```
###### second time
```
docker run redis-stack-server    
```


### 5. Some redis-insight commands 
![img_9.png](img_9.png)
![img_10.png](img_10.png)