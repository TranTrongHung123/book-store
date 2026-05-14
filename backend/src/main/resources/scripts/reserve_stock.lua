-- reserve_stock.lua
-- KEYS[1] = flash:stock:{flashSaleItemId}
-- KEYS[2] = flash:user:{flashSaleItemId}:{userId}
-- KEYS[3] = flash:idempotent:{userId}:{flashSaleItemId}
-- ARGV[1] = số lượng cần giữ
-- ARGV[2] = giới hạn mỗi user
-- ARGV[3] = TTL chống trùng request, tính bằng giây
--
-- Kết quả:
--   1 = thành công
--  -1 = request trùng
--  -2 = vượt giới hạn mỗi user
--  -3 = không đủ stock

-- 1. Chặn request trùng
local idempotent = redis.call('EXISTS', KEYS[3])
if idempotent == 1 then
    return -1
end

local qty = tonumber(ARGV[1])
local maxPerUser = tonumber(ARGV[2])
local idempotentTtl = tonumber(ARGV[3])

-- 2. Kiểm tra giới hạn mỗi user
local currentUserQty = tonumber(redis.call('GET', KEYS[2]) or '0')
if currentUserQty + qty > maxPerUser then
    return -2
end

-- 3. Kiểm tra còn stock không
local currentStock = tonumber(redis.call('GET', KEYS[1]) or '0')
if currentStock < qty then
    return -3
end

-- 4. Hợp lệ thì cập nhật atomically
redis.call('DECRBY', KEYS[1], qty)
redis.call('INCRBY', KEYS[2], qty)
redis.call('SET', KEYS[3], '1', 'EX', idempotentTtl)

return 1
