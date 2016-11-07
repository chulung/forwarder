# forwarder
穿透内网的端口转发工具

这个坑挖了很久了，没有填完，还有一篇文章也没写，现在终于决心把它填上了_(:3J∠)_

# 代码结构

包含三块，base，封装了公共的模块，如编解码等；cs 基于TCP的cs模式；p2p 基于UDP的p2p 模式

- 关于base

codec 编解码模块使用的是kryo，实现来源于

https://github.com/terrymanu/miracle-framework/tree/master/miracle-framework-common/miracle-framework-common-serialize