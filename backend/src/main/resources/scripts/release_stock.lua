-- release_stock.lua
-- KEYS[1] = flash:stock:{flashSaleItemId}
-- KEYS[2] = flash:user:{flashSaleItemId}:{userId}
-- KEYS[3] = flash:reserve:{reservationId}
-- ARGV[1] = số lượng cần hoàn
--
-- Kết quả:
--   1 = thành công, đã hoàn stock
--   0 = reservation đã mất, không cần xử lý

-- 1. Kiểm tra reservation còn tồn tại không
local exists = redis.call('EXISTS', KEYS[3])
if exists == 0 then
    return 0
end

local qty = tonumber(ARGV[1])

-- 2. Hoàn stock và giảm số lượng của user
redis.call('INCRBY', KEYS[1], qty)
local newUserQty = redis.call('DECRBY', KEYS[2], qty)
if tonumber(newUserQty) <= 0 then
    redis.call('DEL', KEYS[2])
end

-- 3. Xóa reservation
redis.call('DEL', KEYS[3])

return 1
