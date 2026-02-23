import { Box, HStack, Text, Circle } from '@chakra-ui/react';
import { useNotifications } from '../../context/NotificationContext';
import { GiCircle } from '../icons';

export default function ConnectionStatus() {
  const { isConnected } = useNotifications();

  return (
    <HStack gap={2} px={3} py={2}>
      <Circle size={2} bg={isConnected ? 'accent.500' : 'red.500'} />
      <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
        {isConnected ? 'Connected' : 'Disconnected'}
      </Text>
    </HStack>
  );
}
