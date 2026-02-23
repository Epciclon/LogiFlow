import { ApiProperty } from "@nestjs/swagger"

export class NotificationResponseDto {
    @ApiProperty()
    id: string
    @ApiProperty()
    eventId: string
    @ApiProperty()
    microservice: string
    @ApiProperty()
    action: string
    @ApiProperty()
    entityType: string
    @ApiProperty()
    entityId: string
    @ApiProperty()
    message: string
    @ApiProperty()
    eventTimestamp: Date
    @ApiProperty()
    createdAt: Date
    @ApiProperty()
    read: boolean
    @ApiProperty()
    processed: boolean
    @ApiProperty({required: false})
    data?: Record<string, any>
    @ApiProperty()
    severity: string
}