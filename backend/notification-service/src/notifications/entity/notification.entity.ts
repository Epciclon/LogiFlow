import { Column, CreateDateColumn, Entity, Index, PrimaryGeneratedColumn } from 'typeorm';

@Entity('notifications')
export class Notification{
    
    @PrimaryGeneratedColumn('uuid')
    id: string;

    @Column({ name: 'event_id', nullable: false })
    @Index()
    eventId: string;

    @Column({ name: 'message', nullable: false })
    message: string;

    @Column({ name: 'action', nullable: false , length: 20})
    action: string;

    @Column({ name: 'microservice', nullable: false})
    microservice: string;

    @Column({ name: 'entity_id', nullable: false})
    @Index()
    entityId: string;

    @Column({ name: 'entity_type', nullable: false})
    entityType: string;

    @CreateDateColumn({ name: 'created_at', type: 'timestamp without time zone' })
    createdAt: Date;

    @Column({ name: 'event_timestamp', type: 'timestamp without time zone'})
    eventTimestamp: Date;

    @Column({ name: 'read', type: 'boolean', default: false })
    read: boolean;

    @Column({ name: 'processed', type: 'boolean', default: false })
    processed: boolean;

    @Column({ name: 'data', type: 'jsonb', nullable: true })
    data: Record<string, any>;

    @Column({ name: 'severity', nullable: true })
    severity: string;
}