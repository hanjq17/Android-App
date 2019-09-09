# Android-App
###使用方式

请用git clone @here 拷贝该项目，用Android Studio打开本项目进行查看和运行。

项目中保存了已经生成的apk，在下方的路径

```
./app-debug.apk
```

###制作者

```
hanjq17@mails.tsinghua.edu.cn		Git:hanjq17		jiaor17@mails.tsinghua.edu.cn   Git:jiaor17
```

### 实现的功能

#### 系统支持

程序在安卓机上正常运行不崩溃 (请避免使用新式手机的分屏操作，会引发闪退)。

#### 分类列表

新闻分类列表支持删除和添加的操作，同时修改时有动态的特效，支持拖动排序。

#### 新闻列表

正确显示新闻列表的消息，图片和视频（如果有的话），布局合理。看过的新闻实现了数据库信息存储，在离线的情况下也可以进行浏览。看过的新闻标记为灰色。

上拉和下拉可以获得更多新闻，有特效。

支持新闻的关键词搜索，关键词搜索有历史记录可供选择和删除。

#### 分享收藏推荐

申请了微信开放平台的AppID，分享内容有新闻标题和图片，点击可以通过新闻url进入到新闻原网址。

新闻详情页面支持收藏的添加和删除，收藏的新闻也可以进行本地的存储，离线情况下也可以进行浏览。

根据用户的观看历史进行新闻推荐，推荐算法为堆关键词进行加权求和，取前三位按照分数分配请求数量，最后再去除重复新闻。

#### 夜间模式

在右上角收起的菜单栏中可以进行夜间模式和日间模式的切换，这个信息也和用户绑定。

#### 新闻朗读

使用科大讯飞的集成包，实现了在线的文字转语音进行阅读新闻的功能，支持点击开始和停止阅读。

#### 登录注册

建立本地数据库，支持用户个性化操作，第一次进入时要进行注册然后登录，观看历史，收藏新闻等等都和用户自己绑定。

#### 屏蔽关键词和新闻

可以在"我的"页面屏蔽关键词，也可以在新闻详情页面屏蔽新闻，默认屏蔽新闻权重最大的关键词。

#### 封面和进场动画

SplashActivity在App打开时会有进场页面和动画效果。

### App截图

![avatar](/Users/hanjq17/Desktop/1.png)

![avatar](/Users/hanjq17/Desktop/2.png)

![avatar](/Users/hanjq17/Desktop/3.png)

![avatar](/Users/hanjq17/Desktop/4.png)

![avatar](/Users/hanjq17/Desktop/6.png)

![avatar](/Users/hanjq17/Desktop/7.png)

![avatar](/Users/hanjq17/Desktop/8.png)

![avatar](/Users/hanjq17/Desktop/9.png)

![avatar](/Users/hanjq17/Desktop/10.png)

![avatar](/Users/hanjq17/Desktop/11.png)

想学iOS开发哦😄~加油！

