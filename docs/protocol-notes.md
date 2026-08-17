# Protocol & Server Notes

## Current state
- Client revision: 32763 (V3)
- Server branch: manifest-v3-local
- Item use opcode: 0x26 (DialogOptionPacket)

## 0x26 packet layout
Hex example: `99130300da493b717d0c0200ff00`

Fields:
- 4 bytes: itemId + slot/flags (high word slot, low word itemId)
- 4 bytes: unknown int
- 4 bytes: unk3 (high word quantity)
- 1 byte: unk4 (-1)
- 1 byte: unk5 (0)

### Mapping
- itemId = unk1 & 0xFFFF
- slotRaw = (unk1 ushr 16) & 0xFFFF
- slot = slotRaw >= 0xC000 ? slotRaw - 0xC000 : slotRaw
- quantity = (unk3 ushr 16) & 0xFFFF

## Item effects
- Potion = 20 HP
- Super Potion = 50 HP
- Hyper Potion = 120 HP
- Max Potion = full HP
- Full Restore = full HP + status cure

## Next steps
- Implement status system for Pokemon.
