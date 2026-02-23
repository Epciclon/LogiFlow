import { Module } from "@nestjs/common";
import { TypeOrmModule } from "@nestjs/typeorm";
import { NotificationService } from "./notification.service";
import { NotificationController } from "./notification.controller";
import { Notification } from "./notification.entity";
import { NotificationsGateway } from "../notifications.gateway";


@Module(
    {
        imports: [TypeOrmModule.forFeature([Notification])],
        controllers: [NotificationController],
        providers: [NotificationService, NotificationsGateway],
        exports: [NotificationService, NotificationsGateway],
    }
    )

export class NotificationModule {}