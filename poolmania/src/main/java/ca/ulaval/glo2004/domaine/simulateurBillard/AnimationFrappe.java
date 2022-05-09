package ca.ulaval.glo2004.domaine.simulateurBillard;

import ca.ulaval.glo2004.domaine.ControleurPoolMania;

public class AnimationFrappe {
    ControleurPoolMania controleur;
    private double animationSteps = 0, animationStepsInit = 0;
    private boolean animationFinished = false;
    private boolean recule = true;

    public AnimationFrappe(ControleurPoolMania controleur) {
        this.controleur = controleur;
    }

    public boolean getAnimationFinished(){
        return animationFinished;
    }
    public void setAnimationFinished(){
        animationFinished = !animationFinished;
    }

    private void setInitValue(){
        animationStepsInit = Math.sqrt(4*controleur.getForce())+ controleur.getRayonBoules();
        animationSteps = Math.sqrt(4*controleur.getForce())+ controleur.getRayonBoules()/4;
    }

    public double getAnimationSteps() {
        if(animationStepsInit == 0){
            setInitValue();
        }
        if(animationSteps < 1){
            animationStepsInit = 0;
            animationSteps = 0;
            animationFinished = true;
            recule = true;
        }
        // Recule la baguette
        else if(recule && animationSteps <= (animationStepsInit + (animationStepsInit*1.25))) {
            animationSteps++;
        }
        else {
            recule = false;
            animationSteps-=(.05*animationStepsInit);
        }
        return animationSteps;
    }


}
