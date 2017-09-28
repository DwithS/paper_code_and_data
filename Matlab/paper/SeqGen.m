function sim_seq = SeqGen(sim_num, sample_num, portion)
% sim_num = 실행할 시뮬레이션 숫자
% sample_num = 샘플 숫자
% portion = 비율
sim_seq=zeros(sample_num, sim_num);
for i = 1:sim_num
    sim_seq(:,i) = crossvalind('HoldOut',sample_num, portion);
end
sim_seq = logical(sim_seq);
end
