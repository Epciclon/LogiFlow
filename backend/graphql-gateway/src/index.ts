import { ApolloServer } from '@apollo/server';
import { startStandaloneServer } from '@apollo/server/standalone';
import { ApolloServerPluginLandingPageLocalDefault } from '@apollo/server/plugin/landingPage/default';
import { typeDefs } from './typeDefs';
import { resolvers } from './resolvers';
import { createLoaders } from './utils/loaders';
import dotenv from 'dotenv';

dotenv.config();

const PORT = parseInt(process.env.PORT || '4000');

interface ContextValue {
    loaders: ReturnType<typeof createLoaders>;
    token?: string;
}

const server = new ApolloServer<ContextValue>({
  typeDefs,
  resolvers,
  introspection: true, // Habilita la introspección para que el Playground funcione
  plugins: [
    ApolloServerPluginLandingPageLocalDefault({ footer: false }) // Habilita el Playground/Sandbox
  ],
});

const startServer = async () => {
    const { url } = await startStandaloneServer(server, {
        listen: { port: PORT },
        context: async ({ req }) => {
            // Extraer token JWT del header Authorization
            const authHeader = req.headers.authorization || '';
            const token = authHeader.startsWith('Bearer ') ? authHeader.substring(7) : authHeader;
            
            return {
                loaders: createLoaders(token || undefined),
                token: token || undefined,
            };
        },
    });

  console.log(`🚀  GraphQL Gateway listo en: ${url}`);
  console.log(`📡  Conectado a microservicios:`);
  console.log(`    - Pedidos: ${process.env.PEDIDO_SERVICE_URL || 'default'}`);
  console.log(`    - Flota: ${process.env.FLEET_SERVICE_URL || 'default'}`);
};

startServer().catch(err => {
    console.error('Error iniciando servidor:', err);
});
