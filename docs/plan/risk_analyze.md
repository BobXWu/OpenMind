## 风险分析 ##


### 01. 技术主管可能长时间缺席项目 ###

        后果 ： 项目可能被延期，或者做出错误的决定  
        
        可能 ： 80%  
        
        影响 ： 9  
        
        曝光 ： 7.2  
        
        方案 ： 要求技术主管尽量每天出席，有问题及时与之沟通  
        
        触发 ： 项目开始时  
        
        责任 ： 项目经理  


### 02. 服务器受到拒绝服务攻击 ###

        后果 ： 服务器宕掉  
        
        可能 ： 50%  
        
        影响 ： 8  
        
        曝光 ： 8  
        
        方案 ：  
            a. 后台运维人员勤于每隔一段时间去查看系统及各服务的状态  
            b. 后台安全人员定期检查  
            c. 使用云服务商提供的云盾  
            d. 为服务器安装防火墙  
        
        触发 ： 服务部署完毕时  
        
        责任 ： 运维人员，安全人员  


### 03. 服务器受到数据库注入攻击 ###

        后果 ：  
            a. 数据泄露  
            b. 脏数据  
            c. 数据大规模破坏  
        
        可能 ： 40%  
        
        影响 ： 10  
        
        曝光 ： 7  
        
        方案 ：  
            a. 质疑一切从客户端而来的数据  
            b. 本项目中，针对nosql注入做防护  
            c. 对于db操作，做预编译来绑定参数  
        
        触发 ： 数据库部署完毕时  
        
        责任 ： 后台编码人员，运维人员，安全人员  


### 04. 时间不够 ###

        后果 ： 未能在指定时间内完成项目进度  
        
        可能 ： 60%  
        
        影响 ： 9  
        
        曝光 ： 9  
        
        方案 ：  
            a. 制定合理的计划表  
            b. 每日完成当天任务，并最好提前做掉一些未来的任务  
            c. 每周周报  
        
        触发 ： 项目开始时  
        
        责任 ： 全员  


### 05. 质量没有达到要求 ###

        后果 ： 废品  
        
        可能 ： 40%  
        
        影响 ： 10  
        
        曝光 ： 7  
        
        方案 ：  
            a. 编码前，详细罗列质量要求  
            b. 严格按照质量要求来  
            c. 前期写一些局部原型  
            d. 编码前，指定编码规范  
            e. 认真做测试  
        
        触发 ： 项目开始时  
        
        责任 ： 全员  


### 06. 遇到难点无法突破 ###

        后果 ： 卡在一个阶段无法推进  
        
        可能 ： 30%  
        
        影响 ： 7  
        
        曝光 ： 9  
        
        方案 ：  
            a. 实在无法解决或解决成本太大的时候，采用退而求其次的方案  
            b. 设计架构的时候，采用比较open的架构方式，便于日后修改  
            c. 对裂化方案做详细的记录  
        
        触发 ： 项目开始时  
        
        责任 ： 全员  


### 07. 中途被迫推倒重来 ###

        后果 ：  
            a. 浪费大量时间  
            b. 严重丧失做下去的动力  
            c. 引发方向性的大争论  
        
        可能 ： 30%  
        
        影响 ： 10  
        
        曝光 ： 9  
        
        方案 ：  
            a. 前期的讨论与设计一定要重视  
            b. 不图一时之快  
            c. 对于需求绝不含糊其辞  
        
        触发 ： 项目开始时  
        
        责任 ： 全员  
