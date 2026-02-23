import { NestFactory } from '@nestjs/core';
import { ValidationPipe } from '@nestjs/common';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';
import { AppModule } from './app.module';

async function bootstrap() {
  // Configurar zona horaria (UTC-5)
  process.env.TZ = 'America/Guayaquil';
  
  const app = await NestFactory.create(AppModule);
  
  // Configurar CORS
  app.enableCors({
    origin: ['http://localhost:3000', 'http://localhost:8000', 'http://localhost:8080'],
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'],
    credentials: true,
  });

  // Configurar validación global
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
      forbidNonWhitelisted: true,
    }),
  );

  // Configurar Swagger
  const config = new DocumentBuilder()
    .setTitle('LogiFlow - Notification Service')
    .setDescription('Microservicio de notificaciones para la plataforma de delivery LogiFlow')
    .setVersion('1.0')
    .addBearerAuth()
    .addTag('notificaciones')
    .addTag('eventos')
    .build();
  
  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('api-docs', app, document);

  // Iniciar servidor
  const port = process.env.PORT || 8087;
  await app.listen(port);
  
  console.log(`╔═══════════════════════════════════════════════════════════════╗`);
  console.log(`║  LogiFlow - Notification Service                              ║`);
  console.log(`╠═══════════════════════════════════════════════════════════════╣`);
  console.log(`║  Servidor: http://localhost:${port.toString().padEnd(40)} ║`);
  console.log(`║  Swagger:  http://localhost:${port}/api-docs${' '.repeat(25)} ║`);
  console.log(`║  Health:   http://localhost:${port}/health${' '.repeat(27)} ║`);
  console.log(`╚═══════════════════════════════════════════════════════════════╝`);
}

bootstrap();