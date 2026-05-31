# 社区超市小程序

社区超市数字化解决方案，支持在线下单、库存管理、智能补货提醒、销售分析。

## 技术栈

- **后端**: Java 17 + Spring Boot 3.2 + MyBatis-Plus + MySQL 8.0
- **前端**: 微信小程序（原生框架）
- **鉴权**: JWT
- **文档**: SpringDoc OpenAPI (Swagger)

## 项目结构

```
community-supermarket/
├── backend/          # Spring Boot 后端
├── miniprogram/      # 微信小程序前端
└── docs/             # 文档
```

## 快速开始

### 后端

1. 配置 MySQL 数据库:
```sql
CREATE DATABASE community_supermarket DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `backend/src/main/resources/application.yml` 中的数据库连接信息

3. 执行数据库初始化脚本: `backend/src/main/resources/db/migration/V1__init.sql`

4. 启动服务:
```bash
cd backend
mvn spring-boot:run
```

5. 访问 API 文档: http://localhost:8080/api/swagger-ui.html

### 前端（微信小程序）

1. 使用微信开发者工具打开 `miniprogram/` 目录
2. 在 `app.js` 中配置后端 API 地址
3. 编译运行

## 核心功能

### C端（用户）
- 商品浏览与搜索
- 购物车管理
- 在线下单
- 订单跟踪

### B端（管理员）
- 商品管理（上架/下架/编辑）
- 库存管理与补货
- 智能补货提醒（基于安全库存 + 季节性系数）
- 订单管理与配送
- 销售报表分析

### 智能补货
- 基于移动平均法分析销售趋势
- 季节性需求系数自动调整安全库存
- 定时检查库存并生成补货提醒
- 计算建议补货量

## API 接口

| 模块 | 接口 | 说明 |
|------|------|------|
| 用户 | POST /api/user/wx-login | 微信登录 |
| 用户 | POST /api/user/dev-login | 开发登录 |
| 商品 | GET /api/product/list | 商品列表 |
| 商品 | GET /api/product/{id} | 商品详情 |
| 订单 | POST /api/order | 创建订单 |
| 订单 | GET /api/order/list | 我的订单 |
| 库存 | GET /api/admin/inventory/list | 库存列表 |
| 补货 | GET /api/admin/restock/alerts | 补货提醒 |
| 统计 | GET /api/admin/stats/sales | 销售报表 |

完整 API 文档请访问 Swagger UI。
