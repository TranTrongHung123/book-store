-- reserve_stock.lua
-- KEYS[1] = flash:stock:{flashSaleItemId}
-- KEYS[2] = flash:user:{flashSaleItemId}:{userId}
-- KEYS[3] = flash:idempotent:{userId}:{flashSaleItemId}
-- ARGV[1] = quantity to reserve
-- ARGV[2] = maxPerUser
-- ARGV[3] = idempotency TTL in seconds
--
-- Returns:
--   1 = success
--  -1 = duplicate request (idempotent)
--  -2 = max per user exceeded
--  -3 = insufficient stock

-- 1. Check idempotency
local idempotent = redis.call('EXISTS', KEYS[3])
if idempotent == 1 then
    return -1
end

local qty = tonumber(ARGV[1])
local maxPerUser = tonumber(ARGV[2])
local idempotentTtl = tonumber(ARGV[3])

-- 2. Check max per user
local currentUserQty = tonumber(redis.call('GET', KEYS[2]) or '0')
if currentUserQty + qty > maxPerUser then
    return -2
end

-- 3. Check stock availability
local currentStock = tonumber(redis.call('GET', KEYS[1]) or '0')
if currentStock < qty then
    return -3
end

-- 4. All checks passed — atomically update
redis.call('DECRBY', KEYS[1], qty)
redis.call('INCRBY', KEYS[2], qty)
redis.call('SET', KEYS[3], '1', 'EX', idempotentTtl)

return 1
