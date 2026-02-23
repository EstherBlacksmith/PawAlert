import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  RadioGroup,
  FormControlLabel,
  Radio,
  Box,
} from '@mui/material'
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
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>Close Alert</DialogTitle>
      <DialogContent>
        <Typography color="text.secondary" sx={{ mb: 2 }}>
          Please select a reason for closing this alert:
        </Typography>
        <RadioGroup
          value={selectedReason || ''}
          onChange={(e) => setSelectedReason(e.target.value as ClosureReason)}
        >
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
            {(Object.keys(closureReasonLabels) as ClosureReason[]).map((reason) => (
              <Box
                key={reason}
                sx={{
                  p: 1.5,
                  border: '1px solid',
                  borderColor: selectedReason === reason ? 'primary.main' : 'divider',
                  borderRadius: 1,
                  cursor: 'pointer',
                  bgcolor: selectedReason === reason ? 'primary.50' : 'transparent',
                  '&:hover': {
                    bgcolor: 'action.hover',
                  },
                }}
                onClick={() => setSelectedReason(reason)}
              >
                <FormControlLabel
                  value={reason}
                  control={<Radio />}
                  label={closureReasonLabels[reason]}
                  sx={{ m: 0 }}
                />
              </Box>
            ))}
          </Box>
        </RadioGroup>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" onClick={handleClose} disabled={isLoading}>
          Cancel
        </Button>
        <Button
          variant="contained"
          color="success"
          onClick={handleConfirm}
          disabled={!selectedReason || isLoading}
        >
          Confirm Close
        </Button>
      </DialogActions>
    </Dialog>
  )
}
