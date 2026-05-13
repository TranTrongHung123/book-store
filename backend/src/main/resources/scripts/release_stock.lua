-- release_stock.lua
-- KEYS[1] = flash:stock:{flashSaleItemId}
-- KEYS[2] = flash:user:{flashSaleItemId}:{userId}
-- KEYS[3] = flash:reserve:{reservationId}
-- ARGV[1] = quantity to release
--
-- Returns:
--   1 = success (stock restored)
--   0 = reservation already gone (idempotent — no action needed)

-- 1. Check if reservation still exists
local exists = redis.call('EXISTS', KEYS[3])
if exists == 0 then
    return 0
end

local qty = tonumber(ARGV[1])

-- 2. Restore stock and user count
redis.call('INCRBY', KEYS[1], qty)
local newUserQty = redis.call('DECRBY', KEYS[2], qty)
if tonumber(newUserQty) <= 0 then
    redis.call('DEL', KEYS[2])
end

-- 3. Delete reservation
redis.call('DEL', KEYS[3])

return 1
