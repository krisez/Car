<h1>关于</h1>
    <li>技术要点
        <ul>
            <li><a href="#arch">框架</a></li>
            <li><a href="#map">地图</a></li>
            <li><a href="#video">视频</a></li>
            <li><a href="#view">自定义View</a></li>
        </ul>
    </li>
    <li><a href="#support">支持库</a></li>
    <li><a href="#video_in">内核</a></li>
</ul>
<hr/>
<h3><a name="arch">框架</a></h3>
<p>整体框架使用Retrofit+MVP,其中Retrofit负责网络请求，对实体类封装请求体，MVP负责整个项目层次的解耦</p>
<p>整个层次差不多这个样子</p>
<img src="../img/arch.png"/>
<hr/>
<h3><a name="map">地图</a></h3>
<p>地图基于<a href="https://lbs.amap.com">高德地图</a>开发，整个API参考官方文案</p>
<p>主要实现的功能有:marker标记，轨迹绘制，动画控制。</p>
<p>其中，动画控制主要实现的是车辆的加速与减速功能，在整个过程中呈现匀变速而非迅速的行驶过程，让用户掌握大概的行驶过程，引起注意。</p>
<hr/>
<h3><a name="video">视频</a></h3>
<p>基于该第三方库<a href="https://github.com/CarGuo/GSYVideoPlayer">GSYVideoPlayer</a>开发</p>
<p>GSYVideoPlayer是基于IJKPlayer进行开发的多功能播放器，同时拥有自定义功能，对界面进行功能扩展。</p>
<p><a href="https://github.com/Bilibili/ijkplayer">IJKPlyaer</a>是国内知名视频弹幕网站Bilibili所开发d的开源项目，其基于FFmpeg进行视频开发，能够在android以及ios进行视频开发，同时对于其so库，因为开源，所以可以自定义自己需要的功能，或者扩展，或者压缩。</p>
<a href="https://blog.csdn.net/qq_34206863/article/details/81564975">主要情况点这里</a>
<hr/>
<h3><a name="view">自定义View</a></h3>
<p>主要用于筛选框的界面动画显示以及下拉刷新的布局处理</p>
<P>筛选框为view+animator一起使用，当view布局结束的时候开始动画</P>
<P>下拉框分为顶部层和内容层。顶部为刷新的头部显示；内容为该界面向用户展示的UI</P>
<hr/>
<h3><a name="support">引用库</a></h3>
<p>网络请求主要是Retrofit</p>
<p>事件线用EventBus</p>
<p>图片加载库是Glide</p>
<hr/>
<h3><a name="video_in">播放内核</a></h3>
<p>app主要使用ijkplayer内核进行播放，当然在设置中也可选择使用系统原生播放器进行播放</p>
