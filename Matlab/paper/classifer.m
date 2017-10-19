function res = classifer(data, answer ,sim_seq, classiferNum)

    if classiferNum == 1
        res = knnClassifier(data, answer ,sim_seq);
    end
    
    if classiferNum ==2
            res = dtClassifier(data, answer ,sim_seq);
    end
    
    if classiferNum ==3
            res = mSvmClassifier(data, answer ,sim_seq);
    end
end