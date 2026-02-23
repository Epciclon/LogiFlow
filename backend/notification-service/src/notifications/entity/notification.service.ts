import { Injectable } from "@nestjs/common";
import { InjectRepository } from "@nestjs/typeorm";
import { Repository } from "typeorm";
import { Notification } from "./notification.entity";
import { CreateNotificationDto } from "../dto/create-notification.dto";

@Injectable()
export class NotificationService {

    constructor(
        @InjectRepository(Notification)
        private notificationRepository: Repository<Notification>,
    ) {}

    async create(dto: CreateNotificationDto): Promise<Notification> {
        const entityNotif = this.notificationRepository.create({...dto});

        return await this.notificationRepository.save(entityNotif);
    }

    async findAll(): Promise<Notification[]> {
        return await this.notificationRepository.find();
    }
}