import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as jwt from 'jsonwebtoken';

@Injectable()
export class JwtAuthGuard implements CanActivate {
  constructor(private readonly configService: ConfigService) {}

  canActivate(context: ExecutionContext): boolean {
    const request = context.switchToHttp().getRequest();
    const authHeader = request.headers['authorization'];

    if (!authHeader) {
      throw new UnauthorizedException('Token de autenticación no proporcionado');
    }

    const token = authHeader.replace('Bearer ', '');
    
    try {
      const secret = this.configService.get<string>('JWT_SECRET');
      
      if (!secret) {
        throw new UnauthorizedException('Configuración de JWT no encontrada');
      }
      
      const decoded = jwt.verify(token, secret);
      request.user = decoded;
      return true;
    } catch (error) {
      if (error.name === 'TokenExpiredError') {
        throw new UnauthorizedException('Token de autenticación expirado');
      }
      throw new UnauthorizedException('Token de autenticación inválido');
    }
  }
}
