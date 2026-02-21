import {
  Box,
  Button,
  Dialog,
  Flex,
  Heading,
  RadioGroup,
  Text,
  VStack,
} from '@chakra-ui/react'
import { useState } from 'react'

export type ClosureReason = 'FOUNDED' | 'FALSE_ALARM' | 'OTHER_REASON'

interface ClosureReasonDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onConfirm: (reason: ClosureReason) => void
  isLoading?: boolean
}

const closureReasonLabels: Record<ClosureReason, string> = {
  FOUNDED: 'Founded - Pet was found',
  FALSE_ALARM: 'False Alarm - Alert was a mistake',
  OTHER_REASON: 'Other Reason - Closed for another reason',
}

export function ClosureReasonDialog({
  open,
  onOpenChange,
  onConfirm,
  isLoading = false,
}: ClosureReasonDialogProps) {
  const [selectedReason, setSelectedReason] = useState<ClosureReason | null>(null)

  const handleConfirm = () => {
    if (selectedReason) {
      onConfirm(selectedReason)
      setSelectedReason(null)
    }
  }

  const handleClose = () => {
    setSelectedReason(null)
    onOpenChange(false)
  }

  return (
    <Dialog.Root open={open} onOpenChange={onOpenChange}>
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content>
          <Dialog.Header>
            <Heading size="md">Close Alert</Heading>
          </Dialog.Header>
          <Dialog.Body>
            <Text mb={4} color="gray.600">
              Please select a reason for closing this alert:
            </Text>
            <VStack align="stretch" gap={3}>
              <RadioGroup.Root
                value={selectedReason || ''}
                onValueChange={(e) => setSelectedReason(e.value as ClosureReason)}
              >
                <VStack align="stretch" gap={2}>
                  {(Object.keys(closureReasonLabels) as ClosureReason[]).map((reason) => (
                    <Box
                      key={reason}
                      p={3}
                      borderWidth="1px"
                      borderRadius="md"
                      cursor="pointer"
                      onClick={() => setSelectedReason(reason)}
                      bg={selectedReason === reason ? 'purple.50' : undefined}
                      borderColor={selectedReason === reason ? 'purple.500' : undefined}
                      _dark={{
                        bg: selectedReason === reason ? 'purple.900' : undefined,
                        borderColor: selectedReason === reason ? 'purple.300' : undefined,
                      }}
                    >
                      <RadioGroup.Item value={reason}>
                        <RadioGroup.ItemHiddenInput />
                        <RadioGroup.ItemIndicator />
                        <RadioGroup.ItemText fontWeight="medium">
                          {closureReasonLabels[reason]}
                        </RadioGroup.ItemText>
                      </RadioGroup.Item>
                    </Box>
                  ))}
                </VStack>
              </RadioGroup.Root>
            </VStack>
          </Dialog.Body>
          <Dialog.Footer>
            <Flex gap={2} justify="flex-end">
              <Button variant="outline" onClick={handleClose} disabled={isLoading}>
                Cancel
              </Button>
              <Button
                colorPalette="green"
                onClick={handleConfirm}
                disabled={!selectedReason || isLoading}
                loading={isLoading}
              >
                Confirm Close
              </Button>
            </Flex>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  )
}
