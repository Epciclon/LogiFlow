import { Module } from '@nestjs/common';
import { RabbitMQService } from './rabbitmq.service';
import { NotificationModule } from '../notifications/entity/notification.module';

@Module({
  imports: [NotificationModule],
  providers: [RabbitMQService],
})
export class RabbitMQModule {}