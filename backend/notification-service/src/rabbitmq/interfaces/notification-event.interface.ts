export interface NotificationEvent {
    eventId: string;
    microservice: string;
    action:string;
    entityType: string;
    entityId: string;
    message: string;
    eventTimestamp: Date;
    data?: Record<string, any>;
    severity?: string;
}